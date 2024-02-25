package org.example.restapplication.service.impl;

import org.example.restapplication.dao.OrderComponentDao;
import org.example.restapplication.dao.impl.OrderComponentDaoImpl;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.service.OrderComponentService;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.mapper.OrderComponentMapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderComponentServiceImpl implements OrderComponentService {
    private final OrderComponentDao orderComponentDao;
    private final OrderComponentMapper mapper;

    public OrderComponentServiceImpl(OrderComponentDao orderComponentDao, OrderComponentMapper mapper) {
        this.orderComponentDao = orderComponentDao;
        this.mapper = mapper;
    }

    public OrderComponentServiceImpl() {
        this.orderComponentDao = new OrderComponentDaoImpl();
        this.mapper = Mappers.getMapper(OrderComponentMapper.class);
    }

    @Override
    public Optional<OrderComponentDto> delete(UUID orderId, UUID sushiId) throws ServiceException {
        try {
            return orderComponentDao.delete(orderId, sushiId).map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderComponentDto> findAllOrderComponents(UUID orderId) throws ServiceException {
        try {
            return orderComponentDao.findAllByOrderId(orderId).stream().map(mapper::toDto).toList();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<OrderComponentDto> findByOrderIdSushiId(UUID orderId, UUID sushiId) throws ServiceException {
        try {
            return orderComponentDao.findByOrderIdSushiId(orderId, sushiId).map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<OrderComponentDto> findById(UUID id) throws ServiceException {
        throw new ServiceException("Need orderId and sushiId");
    }

    @Override
    public List<OrderComponentDto> findAll() throws ServiceException {
        try {
            return orderComponentDao.findAll().stream().map(mapper::toDto).toList();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public OrderComponentDto create(OrderComponentDto dto) throws ServiceException {
        OrderComponent orderComponent = mapper.toOrderComponent(dto);
        try {
            OrderComponent created = orderComponentDao.create(orderComponent);
            return mapper.toDto(created);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<OrderComponentDto> update(OrderComponentDto dto) throws ServiceException {
        OrderComponent orderComponent = mapper.toOrderComponent(dto);
        try {
            return orderComponentDao.update(orderComponent).map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<OrderComponentDto> delete(UUID id) throws ServiceException {
        throw new ServiceException("Need orderId and sushiId");
    }
}
