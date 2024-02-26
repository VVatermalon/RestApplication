package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.ConnectionUtil;
import org.example.restapplication.dao.OrderComponentDao;
import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.dao.mapper.impl.OrderComponentMapperImpl;
import org.example.restapplication.db.ConnectionManager;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.OrderComponent;

import java.sql.*;
import java.util.*;

public class OrderComponentDaoImpl extends OrderComponentDao {
    private static final String SQL_SELECT_ALL_ORDER_COMPONENTS = """
        SELECT O.order_id, O.status, O.total_price,
        S.sushi_id, S.sushi_name, S.price, S.description, T.type_id, T.type_name,
        C.amount
        FROM order_component C JOIN orders O ON C.order_id = O.order_id
        JOIN sushi S ON C.sushi_id = S.sushi_id
        JOIN sushi_type T ON S.type_id = T.type_id""";
    private static final String SQL_SELECT_ORDER_COMPONENT_BY_ORDER_ID = """
    SELECT O.order_id, O.status, O.total_price, 
    S.sushi_id, S.sushi_name, S.price, S.description, T.type_id, T.type_name,
    C.amount
    FROM order_component C JOIN orders O ON C.order_id = O.order_id
    JOIN sushi S ON C.sushi_id = S.sushi_id
    JOIN sushi_type T ON S.type_id = T.type_id
    WHERE O.order_id = ?""";
    private static final String SQL_SELECT_ORDER_COMPONENT_BY_ORDER_ID_SUSHI_ID = """
    SELECT O.order_id, O.status, O.total_price, 
    S.sushi_id, S.sushi_name, S.price, S.description, T.type_id, T.type_name,
    C.amount
    FROM order_component C JOIN orders O ON C.order_id = O.order_id
    JOIN sushi S ON C.sushi_id = S.sushi_id
    JOIN sushi_type T ON S.type_id = T.type_id
    WHERE O.order_id = ? AND S.sushi_id = ?""";
    private static final String SQL_DELETE = """
    DELETE FROM order_component WHERE order_id = ? AND sushi_id = ?""";
    private static final String SQL_CREATE = """
    INSERT INTO order_component(order_id, sushi_id, amount) VALUES (?,?,?)""";
    private static final String SQL_UPDATE = """
    UPDATE order_component SET amount = ? WHERE order_id = ? AND sushi_id = ?""";
    private static final String SQL_ORDER_PRICE_UPDATE = """
    CALL calculate_new_total_price(?)""";
    private final ConnectionManager manager = ConnectionPool.getInstance();
    private static final SimpleResultSetMapper<OrderComponent> mapper = OrderComponentMapperImpl.INSTANCE;

    @Override
    public Optional<OrderComponent> findById(UUID id) throws DaoException{
        throw new DaoException("Need orderId and sushiId");
    }
    @Override
    public Optional<OrderComponent> findByOrderIdSushiId(UUID orderId, UUID sushiId) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ORDER_COMPONENT_BY_ORDER_ID_SUSHI_ID)) {
                statement.setString(1, orderId.toString());
                statement.setString(2, sushiId.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        OrderComponent component = mapper.map(resultSet);
                        return Optional.of(component);
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
    public List<OrderComponent> findAll() throws DaoException {
        Connection connection = null;
        List<OrderComponent> componentList = new ArrayList<>();
        try {
            connection = manager.getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_ORDER_COMPONENTS)) {
                while (resultSet.next()) {
                    OrderComponent component = mapper.map(resultSet);
                    componentList.add(component);
                }
                return componentList;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }
    @Override
    public List<OrderComponent> findAllByOrderId(UUID orderId) throws DaoException {
        Connection connection = null;
        List<OrderComponent> componentList = new ArrayList<>();
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ORDER_COMPONENT_BY_ORDER_ID)) {
                statement.setString(1, orderId.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        OrderComponent component = mapper.map(resultSet);
                        componentList.add(component);
                    }
                    return componentList;
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Optional<OrderComponent> delete(UUID orderId, UUID sushiId) throws DaoException {
        Optional<OrderComponent> component = findByOrderIdSushiId(orderId, sushiId);
        if (component.isEmpty())
            return component;
        Connection connection = null;
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
                statement.setString(1, orderId.toString());
                statement.setString(2, sushiId.toString());
                if (statement.executeUpdate() == 0) {
                    ConnectionUtil.rollback(connection);
                    throw new DaoException();
                }
                setCorrectTotalPriceForOrder(connection, orderId);
                ConnectionUtil.commit(connection);
                return component;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }
    @Override
    public void deleteAllOrderComponents(Connection connection, UUID orderId) throws DaoException {
        List<OrderComponent> componentList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ORDER_COMPONENT_BY_ORDER_ID);
             PreparedStatement deleteStatement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, orderId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderComponent component = mapper.map(resultSet);
                    componentList.add(component);
                }
            }
            for(OrderComponent component:componentList) {
                deleteStatement.setString(1, orderId.toString());
                deleteStatement.setString(2, component.getSushi().getId().toString());
                if (deleteStatement.executeUpdate() == 0) {
                    ConnectionUtil.rollback(connection);
                    throw new DaoException();
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    @Override
    public Optional<OrderComponent> delete(UUID id) throws DaoException {
        throw new DaoException("Need orderId and sushiId");
    }

    @Override
    public OrderComponent create(OrderComponent entity) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(SQL_CREATE)) {
                statement.setString(1, entity.getOrder().getId().toString());
                statement.setString(2, entity.getSushi().getId().toString());
                statement.setInt(3, entity.getAmount());
                if (statement.executeUpdate() == 0) {
                    ConnectionUtil.rollback(connection);
                    throw new DaoException();
                }
                setCorrectTotalPriceForOrder(connection, entity.getOrder().getId());
                ConnectionUtil.commit(connection);
                return findByOrderIdSushiId(entity.getOrder().getId(), entity.getSushi().getId()).get();
            }
        } catch (SQLException | NoSuchElementException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Optional<OrderComponent> update(OrderComponent entity) throws DaoException {
        Optional<OrderComponent> component = findByOrderIdSushiId(entity.getOrder().getId(), entity.getSushi().getId());
        if (component.isEmpty())
            return component;
        Connection connection = null;
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
                statement.setString(2, entity.getOrder().getId().toString());
                statement.setString(3, entity.getSushi().getId().toString());
                statement.setInt(1, entity.getAmount());
                if (statement.executeUpdate() == 0) {
                    ConnectionUtil.rollback(connection);
                    throw new DaoException();
                }
                setCorrectTotalPriceForOrder(connection, entity.getOrder().getId());
                ConnectionUtil.commit(connection);
                return findByOrderIdSushiId(entity.getOrder().getId(), entity.getSushi().getId());
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }
    private void setCorrectTotalPriceForOrder(Connection connection, UUID id) throws DaoException {
        try (CallableStatement statement = connection.prepareCall(SQL_ORDER_PRICE_UPDATE)) {
            statement.setString(1, id.toString());
            if (statement.executeUpdate() == 0) {
                ConnectionUtil.rollback(connection);
                throw new DaoException();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
