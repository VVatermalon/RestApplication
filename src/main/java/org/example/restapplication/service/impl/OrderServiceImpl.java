package org.example.restapplication.service.impl;

import org.example.restapplication.dao.OrderDao;
import org.example.restapplication.dao.impl.OrderDaoImpl;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.service.OrderComponentService;
import org.example.restapplication.service.OrderService;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.dto.OrderDto;
import org.example.restapplication.servlet.mapper.OrderMapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final OrderMapper mapper;

    public OrderServiceImpl(OrderDao orderDao, OrderMapper mapper) {
        this.orderDao = orderDao;
        this.mapper = mapper;
    }

    public OrderServiceImpl() {
        this.orderDao = new OrderDaoImpl();
        this.mapper = Mappers.getMapper(OrderMapper.class);
    }

    @Override
    public Optional<OrderDto> findById(UUID id) throws ServiceException {
        try {
            Optional<Order> found = orderDao.findById(id);
            if (found.isEmpty())
                return Optional.empty();
            return found.map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderDto> findAll() throws ServiceException {
        try {
            List<Order> orders = orderDao.findAll();
            return orders.stream().map(mapper::toDto).toList();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public OrderDto create(OrderDto dto) throws ServiceException {
        Order order = mapper.toOrder(dto);
        try {
            return mapper.toDto(orderDao.create(order));
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public OrderDto create() throws ServiceException {
        OrderDto dto = new OrderDto(null, Order.OrderStatus.IN_PROCESS, BigDecimal.ZERO, Collections.emptyList());
        return create(dto);
    }

    @Override
    public Optional<OrderDto> update(OrderDto dto) throws ServiceException {
        Order order = mapper.toOrder(dto);
        try {
            return orderDao.update(order).isPresent()?findById(dto.getId()):Optional.empty();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<OrderDto> delete(UUID id) throws ServiceException {
        try {
            Optional<OrderDto> found = findById(id);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            OrderDto order = found.get();
            return orderDao.delete(order.getId()).isPresent() ? found : Optional.empty();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
