package org.example.restapplication.dao;

import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.OrderComponent;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class OrderComponentDao extends AbstractDao<UUID, OrderComponent> {
    public abstract List<OrderComponent> findAllByOrderId(UUID orderId) throws DaoException;
    public abstract Optional<OrderComponent> delete(UUID orderId, UUID sushiId) throws DaoException;
    public abstract Optional<OrderComponent> findByOrderIdSushiId(UUID orderId, UUID sushiId) throws DaoException;
    public abstract void deleteAllOrderComponents(Connection connection, UUID orderId) throws DaoException;
}
