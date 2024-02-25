package org.example.restapplication.dao;

import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.DaoException;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionUtil {
    public static void close(Connection connection) throws DaoException {
        if(connection == null) {
            throw new DaoException("Connection value is null");
        }
        try {
            if(!connection.getAutoCommit()) {
                rollback(connection);
                connection.setAutoCommit(true);
            }
            ConnectionPool.getInstance().releaseConnection(connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
    public static void commit(Connection connection) throws DaoException {
        if(connection == null) {
            throw new DaoException("Connection value is null");
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
    public static void rollback(Connection connection) throws DaoException {
        if(connection == null) {
            throw new DaoException("Connection value is null");
        }
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
