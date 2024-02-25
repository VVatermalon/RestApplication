package org.example.restapplication.service;

import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.servlet.dto.OrderDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    Optional<OrderDto> findById(UUID id) throws ServiceException;

    List<OrderDto> findAll() throws ServiceException;

    OrderDto create(OrderDto dto) throws ServiceException;

    Optional<OrderDto> update(OrderDto dto) throws ServiceException;

    Optional<OrderDto> delete(UUID id) throws ServiceException;

    OrderDto create() throws ServiceException;
}
