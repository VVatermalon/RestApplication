package org.example.restapplication.dao.mapper.impl;

import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;

import java.sql.ResultSet;
import java.sql.SQLException;

public enum OrderComponentMapperImpl implements SimpleResultSetMapper<OrderComponent> {
    INSTANCE;

    private static final String AMOUNT_LABEL = "amount";

    public OrderComponent map(ResultSet resultSet) throws DaoException {
        OrderComponent orderComponent;
        try {
            Order order = OrderMapperImpl.INSTANCE.map(resultSet);
            Sushi sushi = SushiMapperImpl.INSTANCE.map(resultSet);
            int amount = resultSet.getInt(AMOUNT_LABEL);
            orderComponent = new OrderComponent(order, sushi, amount);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return orderComponent;
    }
}
