package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.ConnectionUtil;
import org.example.restapplication.dao.SushiTypeDao;
import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.dao.mapper.impl.SushiTypeMapperImpl;
import org.example.restapplication.db.ConnectionManager;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.SushiType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SushiTypeDaoImpl extends SushiTypeDao {
    private static final String SQL_SELECT_ALL_TYPES = """
    SELECT T.type_id, T.type_name
    FROM sushi_type T""";
    private static final String SQL_SELECT_TYPE_BY_ID = """
    SELECT T.type_id, T.type_name
    FROM sushi_type T
    WHERE T.type_id = ?""";
    private static final String SQL_DELETE = """
        DELETE FROM sushi_type WHERE type_id = ?""";
    private static final String SQL_CREATE = """
        INSERT INTO sushi_type(type_id, type_name) VALUES (uuid(),?)""";
    private static final String SQL_UPDATE = """
        UPDATE sushi_type SET type_name = ? WHERE type_id = ?""";
    private final ConnectionManager manager = ConnectionPool.getInstance();
    private static final SimpleResultSetMapper<SushiType> mapper = SushiTypeMapperImpl.INSTANCE;
    @Override
    public Optional<SushiType> findById(UUID id) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_TYPE_BY_ID)) {
                statement.setString(1, id.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        SushiType type = mapper.map(resultSet);
                        return Optional.of(type);
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
    public List<SushiType> findAll() throws DaoException {
        Connection connection = null;
        List<SushiType> typeList = new ArrayList<>();
        try {
            connection = manager.getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_TYPES)) {
                while (resultSet.next()) {
                    SushiType type = mapper.map(resultSet);
                    typeList.add(type);
                }
                return typeList;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Optional<SushiType> delete(UUID id) throws DaoException {
        Optional<SushiType> type = findById(id);
        if (type.isEmpty())
            return type;
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
        return type;
    }

    @Override
    public SushiType create(SushiType entity) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, entity.getName());
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    keys.next();
                    UUID generatedId = UUID.fromString(keys.getString(1));
                    entity.setId(generatedId);
                    return entity;
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Optional<SushiType> update(SushiType entity) throws DaoException {
        Optional<SushiType> type = findById(entity.getId());
        if (type.isEmpty())
            return type;
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
                statement.setString(1, entity.getName());
                statement.setString(2, entity.getId().toString());
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
