package org.example.restapplication.service;

import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.servlet.dto.OrderComponentDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderComponentService {
    Optional<OrderComponentDto> findById(UUID id) throws ServiceException;

    List<OrderComponentDto> findAll() throws ServiceException;

    OrderComponentDto create(OrderComponentDto dto) throws ServiceException;

    Optional<OrderComponentDto> update(OrderComponentDto dto) throws ServiceException;

    Optional<OrderComponentDto> delete(UUID id) throws ServiceException;

    Optional<OrderComponentDto> delete(UUID orderId, UUID sushiId) throws ServiceException;

    List<OrderComponentDto> findAllOrderComponents(UUID orderId) throws ServiceException;

    Optional<OrderComponentDto> findByOrderIdSushiId(UUID orderId, UUID sushiId) throws ServiceException;
}
