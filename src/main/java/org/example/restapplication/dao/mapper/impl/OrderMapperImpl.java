package org.example.restapplication.dao.mapper.impl;

import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Order;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public enum OrderMapperImpl implements SimpleResultSetMapper<Order> {
    INSTANCE;

    private static final String ID_LABEL = "order_id";
    private static final String STATUS_LABEL = "status";
    private static final String PRICE_LABEL = "price";

    public Order map(ResultSet resultSet) throws DaoException {
        Order order;
        try {
            UUID id = resultSet.getObject(ID_LABEL, UUID.class);
            String statusStr = resultSet.getString(STATUS_LABEL).trim();
            BigDecimal totalPrice = resultSet.getBigDecimal(PRICE_LABEL);
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr);
            order = new Order(id, status, totalPrice);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return order;
    }
}
