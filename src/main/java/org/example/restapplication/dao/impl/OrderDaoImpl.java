package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.ConnectionUtil;
import org.example.restapplication.dao.OrderComponentDao;
import org.example.restapplication.dao.OrderDao;
import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.dao.mapper.impl.OrderMapperImpl;
import org.example.restapplication.db.ConnectionManager;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class OrderDaoImpl extends OrderDao {
    private static final String SQL_SELECT_ALL_ORDERS = """
    SELECT order_id, status, total_price
    FROM orders""";
    private static final String SQL_SELECT_ORDER_BY_ID = """
    SELECT order_id, status, total_price FROM orders
    WHERE order_id = ?""";
    private static final String SQL_DELETE = """
    DELETE FROM orders WHERE order_id = ?""";
    private static final String SQL_CREATE = """
    INSERT INTO orders(order_id, status, total_price) VALUES (?,?,?)""";
    private static final String SQL_UPDATE = """
    UPDATE orders SET status = ? WHERE order_id = ?""";
    private final ConnectionManager manager = ConnectionPool.getInstance();
    private final OrderComponentDao orderComponentDao = new OrderComponentDaoImpl();
    private static final SimpleResultSetMapper<Order> mapper = OrderMapperImpl.INSTANCE;

    @Override
    public Optional<Order> findById(UUID id) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ORDER_BY_ID)) {
                statement.setString(1, id.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Order order = mapper.map(resultSet);
                        return Optional.of(order);
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
    public List<Order> findAll() throws DaoException {
        Connection connection = null;
        List<Order> orderList = new ArrayList<>();
        try {
            connection = manager.getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_ORDERS)) {
                while (resultSet.next()) {
                    Order order = mapper.map(resultSet);
                    orderList.add(order);
                }
                return orderList;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Optional<Order> delete(UUID id) throws DaoException {
        Optional<Order> order = findById(id);
        if (order.isEmpty())
            return order;
        Connection connection = null;
        try {
            connection = manager.getConnection();
            connection.setAutoCommit(false);
            orderComponentDao.deleteAllOrderComponents(connection, id);
            try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
                statement.setString(1, id.toString());
                if (statement.executeUpdate() == 0) {
                    ConnectionUtil.rollback(connection);
                    throw new DaoException();
                }
            }
            ConnectionUtil.commit(connection);
            return order;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            ConnectionUtil.close(connection);
        }
    }

    @Override
    public Order create(Order entity) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(SQL_CREATE)) {
                UUID newId = UUID.randomUUID();
                statement.setString(1, newId.toString());
                statement.setString(2, entity.getStatus().toString());
                statement.setBigDecimal(3, BigDecimal.ZERO);
                if(statement.executeUpdate()==0) {
                    throw new DaoException("Error during creation of SushiType");
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
    public Optional<Order> update(Order entity) throws DaoException {
        Connection connection = null;
        try {
            connection = manager.getConnection();
            Optional<Order> order = findById(entity.getId());
            if (order.isEmpty())
                return order;
            try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
                statement.setString(1, entity.getStatus().toString());
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
