package org.example.restapplication.service.impl;

import org.example.restapplication.dao.OrderComponentDao;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.mapper.OrderComponentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderComponentServiceImplTest {
    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID SUSHI_ID = UUID.randomUUID();
    private static final UUID ORDER_COMPONENT_ID = UUID.randomUUID();
    private static final OrderComponentDto ORDER_COMPONENT_DTO_DEFAULT = new OrderComponentDto();
    private static final OrderComponent ORDER_COMPONENT_DEFAULT = new OrderComponent();
    private OrderComponentServiceImpl service;

    @Mock
    private OrderComponentDao orderComponentDao;
    @Mock
    private OrderComponentMapper mapper;

    @BeforeEach
    void setUp() {
        service = new OrderComponentServiceImpl(orderComponentDao, mapper);
    }

    @Test
    void delete() throws DaoException, ServiceException {
        when(orderComponentDao.delete(ORDER_ID, SUSHI_ID)).thenReturn(Optional.of(ORDER_COMPONENT_DEFAULT));
        when(mapper.toDto(ORDER_COMPONENT_DEFAULT)).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var actual = service.delete(ORDER_ID, SUSHI_ID);

        verify(orderComponentDao, times(1)).delete(ORDER_ID, SUSHI_ID);
        verify(mapper, times(1)).toDto(ORDER_COMPONENT_DEFAULT);
        assertEquals(Optional.of(ORDER_COMPONENT_DTO_DEFAULT), actual);
    }

    @Test
    void findAllOrderComponents() throws DaoException, ServiceException {
        List<OrderComponent> orderComponents = Collections.singletonList(ORDER_COMPONENT_DEFAULT);
        when(orderComponentDao.findAllByOrderId(ORDER_ID)).thenReturn(orderComponents);
        when(mapper.toDto(ORDER_COMPONENT_DEFAULT)).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var actual = service.findAllOrderComponents(ORDER_ID);

        verify(orderComponentDao, times(1)).findAllByOrderId(ORDER_ID);
        verify(mapper, times(1)).toDto(ORDER_COMPONENT_DEFAULT);
        assertEquals(List.of(ORDER_COMPONENT_DTO_DEFAULT), actual);
    }

    @Test
    void findByOrderIdSushiId() throws DaoException, ServiceException {
        when(orderComponentDao.findByOrderIdSushiId(ORDER_ID, SUSHI_ID)).thenReturn(Optional.of(ORDER_COMPONENT_DEFAULT));
        when(mapper.toDto(ORDER_COMPONENT_DEFAULT)).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var actual = service.findByOrderIdSushiId(ORDER_ID, SUSHI_ID);

        verify(orderComponentDao, times(1)).findByOrderIdSushiId(ORDER_ID, SUSHI_ID);
        verify(mapper, times(1)).toDto(ORDER_COMPONENT_DEFAULT);
        assertEquals(Optional.of(ORDER_COMPONENT_DTO_DEFAULT), actual);
    }

    @Test
    void findAll() throws DaoException, ServiceException {
        List<OrderComponent> orderComponents = Collections.singletonList(ORDER_COMPONENT_DEFAULT);
        when(orderComponentDao.findAll()).thenReturn(orderComponents);
        when(mapper.toDto(ORDER_COMPONENT_DEFAULT)).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var actual = service.findAll();

        verify(orderComponentDao, times(1)).findAll();
        verify(mapper, times(1)).toDto(ORDER_COMPONENT_DEFAULT);
        assertEquals(List.of(ORDER_COMPONENT_DTO_DEFAULT), actual);
    }

    @Test
    void create() throws DaoException, ServiceException {
        when(mapper.toOrderComponent(ORDER_COMPONENT_DTO_DEFAULT)).thenReturn(ORDER_COMPONENT_DEFAULT);
        when(orderComponentDao.create(ORDER_COMPONENT_DEFAULT)).thenReturn(ORDER_COMPONENT_DEFAULT);
        when(mapper.toDto(ORDER_COMPONENT_DEFAULT)).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var actual = service.create(ORDER_COMPONENT_DTO_DEFAULT);

        verify(mapper, times(1)).toOrderComponent(ORDER_COMPONENT_DTO_DEFAULT);
        verify(orderComponentDao, times(1)).create(ORDER_COMPONENT_DEFAULT);
        verify(mapper, times(1)).toDto(ORDER_COMPONENT_DEFAULT);
        assertEquals(ORDER_COMPONENT_DTO_DEFAULT, actual);
    }

    @Test
    void update() throws DaoException, ServiceException {
        when(mapper.toOrderComponent(ORDER_COMPONENT_DTO_DEFAULT)).thenReturn(ORDER_COMPONENT_DEFAULT);
        when(orderComponentDao.update(ORDER_COMPONENT_DEFAULT)).thenReturn(Optional.of(ORDER_COMPONENT_DEFAULT));
        when(mapper.toDto(ORDER_COMPONENT_DEFAULT)).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var actual = service.update(ORDER_COMPONENT_DTO_DEFAULT);

        verify(mapper, times(1)).toOrderComponent(ORDER_COMPONENT_DTO_DEFAULT);
        verify(orderComponentDao, times(1)).update(ORDER_COMPONENT_DEFAULT);
        verify(mapper, times(1)).toDto(ORDER_COMPONENT_DEFAULT);
        assertEquals(Optional.of(ORDER_COMPONENT_DTO_DEFAULT), actual);
    }

    @Test
    void findById() {
        assertThrows(ServiceException.class, () -> {
            service.findById(UUID.randomUUID());
        });
    }

    @Test
    void testDelete() {
        assertThrows(ServiceException.class, () -> {
            service.delete(UUID.randomUUID());
        });
    }
}
