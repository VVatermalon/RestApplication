package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.ConnectionUtil;
import org.example.restapplication.dao.OrderComponentDao;
import org.example.restapplication.dao.OrderDao;
import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.dao.mapper.impl.OrderMapperImpl;
import org.example.restapplication.dao.mapper.impl.SushiMapperImpl;
import org.example.restapplication.db.ConnectionManager;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.Sushi;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class OrderDaoImpl extends OrderDao {
    private static final String SQL_SELECT_ALL_ORDERS = """
    SELECT order_id, status, total_price
    FROM orders""";
    private static final String SQL_SELECT_SUSHI_BY_ORDER_ID = """
    SELECT S.sushi_id, S.sushi_name, S.price, S.description, T.type_id, T.type_name
    FROM sushi S JOIN sushi_type T ON S.type_id = T.type_id
    JOIN order_component C ON C.sushi_id = S.sushi_id
    WHERE C.order_id = ?""";
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
    private static final SimpleResultSetMapper<Order> mapper = OrderMapperImpl.INSTANCE;
    private static final SimpleResultSetMapper<Sushi> sushiMapper = SushiMapperImpl.INSTANCE;

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
                        order.setComponents(findAllSushiByOrderId(connection, id));
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
                    order.setComponents(findAllSushiByOrderId(connection, order.getId()));
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
            try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
                statement.setString(1, id.toString());
                if (statement.executeUpdate() == 0) {
                    throw new DaoException();
                }
            }
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
    private List<Sushi> findAllSushiByOrderId(Connection connection, UUID id) throws DaoException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_SUSHI_BY_ORDER_ID)) {
            preparedStatement.setString(1, id.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Sushi> sushi = new ArrayList<>();
            while (resultSet.next()) {
                sushi.add(sushiMapper.map(resultSet));
            }
            return sushi;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}
