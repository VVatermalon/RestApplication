package org.example.restapplication.db;

import org.example.restapplication.exception.ConnectionPoolException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static final String DRIVER_NAME_PROPERTY_NAME = "db.driver";
    private static final String DATABASE_URL_PROPERTY_NAME = "db.url";
    private static String DATABASE_URL;
    private static Properties connectionProperties;

    static {
        refreshProperties();
    }

    private ConnectionFactory() {}

    static Connection create() throws ConnectionPoolException {
        if(!DATABASE_URL.equals(connectionProperties.getProperty(DATABASE_URL_PROPERTY_NAME))) {
            refreshProperties();
        }
        try {
            return DriverManager.getConnection(DATABASE_URL, connectionProperties);
        } catch (SQLException e) {
            throw new ConnectionPoolException(e);
        }
    }
    private static void refreshProperties() {
        connectionProperties = PropertiesUtil.getProperties();
        try {
            String driverName = connectionProperties.getProperty(DRIVER_NAME_PROPERTY_NAME);
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        DATABASE_URL = connectionProperties.getProperty(DATABASE_URL_PROPERTY_NAME);
    }
}