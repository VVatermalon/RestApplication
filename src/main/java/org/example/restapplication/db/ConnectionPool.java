package org.example.restapplication.db;

import org.example.restapplication.exception.ConnectionPoolException;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool implements ConnectionManager {
    private static final String DATABASE_PROPERTIES_PATH = "db";
    private static final String POOL_SIZE_PROPERTY_NAME = "pool.size";
    private static final long TIME_TO_CHECK_CONNECTIONS = 60 * (long) 60 * 1000;
    private static final int DEFAULT_POOL_SIZE;
    private static final AtomicBoolean create = new AtomicBoolean(false);
    private static final ReentrantLock lockerCreator = new ReentrantLock();
    private static ConnectionPool instance;
    private final BlockingQueue<Connection> freeConnections;
    private final Queue<Connection> givenAwayConnections;
    private final Timer checkConnectionsTimer = new Timer(true);

    static {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(DATABASE_PROPERTIES_PATH);
            DEFAULT_POOL_SIZE = Integer.parseInt(bundle.getString(POOL_SIZE_PROPERTY_NAME));
        } catch (MissingResourceException | NumberFormatException e) {
            throw new RuntimeException("Error during properties reading", e);
        }
    }

    private ConnectionPool() {
        freeConnections = new LinkedBlockingDeque<>(DEFAULT_POOL_SIZE);
        givenAwayConnections = new ArrayDeque<>(DEFAULT_POOL_SIZE);

        boolean attemptFlag = false;
        while (freeConnections.size() != DEFAULT_POOL_SIZE) {
            try {
                Connection connection = ConnectionFactory.create();
                freeConnections.add(connection);
            } catch (ConnectionPoolException e) {
                if (attemptFlag && freeConnections.isEmpty()) {
                    throw new RuntimeException("Error during connection establishing", e);
                }
                attemptFlag = true;
            }
        }
        createTimerTask();
    }

    public static ConnectionPool getInstance() {
        if (!create.get()) {
            try {
                lockerCreator.lock();
                if (instance == null) {
                    instance = new ConnectionPool();
                    create.set(true);
                }
            } finally {
                lockerCreator.unlock();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = freeConnections.take();
            givenAwayConnections.offer(connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return connection;
    }

    public void releaseConnection(Connection connection) {
        givenAwayConnections.remove(connection);
        try {
            freeConnections.put(connection);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void destroyPool() {
        checkConnectionsTimer.cancel();
        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            try {
                freeConnections.take().close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (SQLException e) {
            }
        }
        deregisterDrivers();
    }

    private void deregisterDrivers() {
        while (DriverManager.getDrivers().hasMoreElements()) {
            Driver driver = DriverManager.getDrivers().nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
            }
        }
    }

    private void createTimerTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    Iterator<Connection> iterator = freeConnections.iterator();
                    while (iterator.hasNext()) {
                        Connection connection = iterator.next();
                        if (!connection.isValid(2)) {
                            connection.close();
                            iterator.remove();
                            Connection newConnection = ConnectionFactory.create();
                            freeConnections.add(newConnection);
                        }
                    }
                } catch (SQLException | ConnectionPoolException e) {
                    throw new RuntimeException("Error during checking connections timer task working", e);
                }
            }
        };
        checkConnectionsTimer.scheduleAtFixedRate(timerTask, TIME_TO_CHECK_CONNECTIONS, TIME_TO_CHECK_CONNECTIONS);
    }
}