package org.example.restapplication.service.impl;

import org.example.restapplication.dao.OrderDao;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.service.OrderComponentService;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.dto.OrderDto;
import org.example.restapplication.servlet.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID ORDER_COMPONENT_ID = UUID.randomUUID();
    private static final OrderDto ORDER_DTO_DEFAULT = new OrderDto(ORDER_ID, Order.OrderStatus.IN_PROCESS, BigDecimal.ZERO, Collections.emptyList());
    private static final Order ORDER_DEFAULT = new Order(ORDER_ID, Order.OrderStatus.IN_PROCESS, BigDecimal.ZERO);
    private static final OrderComponentDto ORDER_COMPONENT_DTO_DEFAULT = new OrderComponentDto();
    private static final List<OrderComponentDto> ORDER_COMPONENTS_LIST_DEFAULT = List.of(ORDER_COMPONENT_DTO_DEFAULT);
    private OrderServiceImpl service;

    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderComponentService orderComponentService;
    @Mock
    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(orderDao, orderComponentService, mapper);
    }

    @Test
    void findById() throws DaoException, ServiceException {
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(ORDER_DEFAULT));
        when(orderComponentService.findAllOrderComponents(ORDER_ID)).thenReturn(ORDER_COMPONENTS_LIST_DEFAULT);
        when(mapper.toDto(ORDER_DEFAULT)).thenReturn(ORDER_DTO_DEFAULT);

        var actual = service.findById(ORDER_ID);

        verify(orderDao, times(1)).findById(ORDER_ID);
        verifyNoMoreInteractions(orderDao);
        verify(orderComponentService, times(1)).findAllOrderComponents(ORDER_ID);
        verifyNoMoreInteractions(orderComponentService);
        verify(mapper, times(1)).toDto(ORDER_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(Optional.of(ORDER_DTO_DEFAULT), actual);
    }

    @Test
    void findAll() throws DaoException, ServiceException {
        List<Order> orders = List.of(ORDER_DEFAULT);
        when(orderDao.findAll()).thenReturn(orders);
        when(orderComponentService.findAllOrderComponents(ORDER_ID)).thenReturn(ORDER_COMPONENTS_LIST_DEFAULT);
        when(mapper.toDto(ORDER_DEFAULT)).thenReturn(ORDER_DTO_DEFAULT);

        var actual = service.findAll();

        verify(orderDao, times(1)).findAll();
        verifyNoMoreInteractions(orderDao);
        verify(orderComponentService, times(1)).findAllOrderComponents(ORDER_ID);
        verifyNoMoreInteractions(orderComponentService);
        verify(mapper, times(1)).toDto(ORDER_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(List.of(ORDER_DTO_DEFAULT), actual);
    }

    @Test
    void create() throws DaoException, ServiceException {
        when(mapper.toOrder(ORDER_DTO_DEFAULT)).thenReturn(ORDER_DEFAULT);
        when(orderDao.create(ORDER_DEFAULT)).thenReturn(ORDER_DEFAULT);
        when(mapper.toDto(ORDER_DEFAULT)).thenReturn(ORDER_DTO_DEFAULT);

        var actual = service.create(ORDER_DTO_DEFAULT);

        verify(mapper, times(1)).toOrder(ORDER_DTO_DEFAULT);
        verify(orderDao, times(1)).create(ORDER_DEFAULT);
        verify(mapper, times(1)).toDto(ORDER_DEFAULT);
        assertEquals(ORDER_DTO_DEFAULT, actual);
    }

    @Test
    void createWithoutParameters() throws ServiceException, DaoException {
        when(mapper.toOrder(any())).thenReturn(ORDER_DEFAULT);
        when(orderDao.create(ORDER_DEFAULT)).thenReturn(ORDER_DEFAULT);
        when(mapper.toDto(ORDER_DEFAULT)).thenReturn(ORDER_DTO_DEFAULT);

        var actual = service.create();

        verify(mapper, times(1)).toOrder(any());
        verify(orderDao, times(1)).create(ORDER_DEFAULT);
        verify(mapper, times(1)).toDto(ORDER_DEFAULT);
        assertEquals(ORDER_DTO_DEFAULT, actual);
    }

    @Test
    void update() throws DaoException, ServiceException {
        when(mapper.toOrder(ORDER_DTO_DEFAULT)).thenReturn(ORDER_DEFAULT);
        when(orderDao.update(ORDER_DEFAULT)).thenReturn(Optional.of(ORDER_DEFAULT));
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(ORDER_DEFAULT));
        when(orderComponentService.findAllOrderComponents(ORDER_ID)).thenReturn(ORDER_COMPONENTS_LIST_DEFAULT);
        when(mapper.toDto(ORDER_DEFAULT)).thenReturn(ORDER_DTO_DEFAULT);

        var actual = service.update(ORDER_DTO_DEFAULT);

        verify(mapper, times(1)).toOrder(ORDER_DTO_DEFAULT);
        verify(orderDao, times(1)).update(ORDER_DEFAULT);
        assertEquals(Optional.of(ORDER_DTO_DEFAULT), actual);
    }

    @Test
    void delete() throws DaoException, ServiceException {
        ORDER_DEFAULT.setId(ORDER_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(Optional.of(ORDER_DEFAULT));
        when(orderDao.delete(ORDER_ID)).thenReturn(Optional.of(ORDER_DEFAULT));
        when(mapper.toDto(ORDER_DEFAULT)).thenReturn(ORDER_DTO_DEFAULT);

        var actual = service.delete(ORDER_ID);

        verify(orderDao, times(1)).findById(ORDER_ID);
        verify(orderDao, times(1)).delete(ORDER_ID);
        assertEquals(Optional.of(ORDER_DTO_DEFAULT), actual);
    }
}
