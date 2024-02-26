package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.ConnectionUtil;
import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.dao.mapper.impl.SushiMapperImpl;
import org.example.restapplication.db.ConnectionManager;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.dao.SushiDao;

import java.sql.*;
import java.util.*;

public class SushiDaoImpl extends SushiDao {
    private static final String SQL_SELECT_ALL_SUSHI = """
    SELECT S.sushi_id, S.sushi_name, S.price, S.description, T.type_id, T.type_name
    FROM sushi S JOIN sushi_type T ON S.type_id = T.type_id""";
    private static final String SQL_SELECT_SUSHI_BY_ID = """
    SELECT S.sushi_id, S.sushi_name, S.price, S.description, T.type_id, T.type_name
    FROM sushi S JOIN sushi_type T ON S.type_id = T.type_id
    WHERE S.sushi_id = ?""";
    private static final String SQL_SELECT_SUSHI_BY_TYPE_ID = """
    SELECT S.sushi_id, S.sushi_name, S.price, S.description, T.type_id, T.type_name
    FROM sushi S JOIN sushi_type T ON S.type_id = T.type_id
    WHERE T.type_id = ?""";
    private static final String SQL_DELETE = """
        DELETE FROM sushi WHERE sushi_id = ?""";
    private static final String SQL_CREATE = """
        INSERT INTO sushi(sushi_id, sushi_name, price, description, type_id) VALUES (?, ?,?,?,?)""";
    private static final String SQL_UPDATE = """
        UPDATE sushi SET sushi_name = ?, price = ?, description = ?, type_id = ? WHERE sushi_id = ?""";
    private final ConnectionManager manager = ConnectionPool.getInstance();
    private static final SimpleResultSetMapper<Sushi> mapper = SushiMapperImpl.INSTANCE;
    @Override
    public Optional<Sushi> findById(UUID id) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_SUSHI_BY_ID)) {
                statement.setString(1, id.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Sushi sushi = mapper.map(resultSet);
                        return Optional.of(sushi);
                    }
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public List<Sushi> findAll() throws DaoException {
        Connection connection = null;
        List<Sushi> sushiList = new ArrayList<>();
        try {
            connection = manager.getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_SUSHI)) {
                while (resultSet.next()) {
                    Sushi sushi = mapper.map(resultSet);
                    sushiList.add(sushi);
                }
                return sushiList;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }
    @Override
    public List<Sushi> findSushiByTypeId(UUID id) throws DaoException {
        Connection connection = null;
        List<Sushi> sushiList = new ArrayList<>();
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_SUSHI_BY_TYPE_ID)) {
                statement.setString(1, id.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Sushi sushi = mapper.map(resultSet);
                        sushiList.add(sushi);
                    }
                }
                return sushiList;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Optional<Sushi> delete(UUID id) throws DaoException {
        Optional<Sushi> sushi = findById(id);
        if (sushi.isEmpty())
            return sushi;
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
                statement.setString(1, id.toString());
                if (statement.executeUpdate() == 0) {
                    throw new DaoException();
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
        return sushi;
    }

    @Override
    public Sushi create(Sushi entity) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_CREATE)) {
                UUID newId = UUID.randomUUID();
                statement.setString(1, newId.toString());
                statement.setString(2, entity.getName());
                statement.setBigDecimal(3, entity.getPrice());
                statement.setString(4, entity.getDescription());
                statement.setString(5, entity.getType().getId().toString());
                if(statement.executeUpdate()==0) {
                    throw new DaoException("Error during creation of Sushi");
                }
                return findById(newId).get();
            }
        } catch (SQLException | NoSuchElementException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Optional<Sushi> update(Sushi entity) throws DaoException {
        Optional<Sushi> sushi = findById(entity.getId());
        if (sushi.isEmpty())
            return sushi;
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
                statement.setString(1, entity.getName());
                statement.setBigDecimal(2, entity.getPrice());
                statement.setString(3, entity.getDescription());
                statement.setString(4, entity.getType().getId().toString());
                statement.setString(5, entity.getId().toString());
                if (statement.executeUpdate() == 0) {
                    throw new DaoException();
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
        return Optional.of(entity);
    }
}