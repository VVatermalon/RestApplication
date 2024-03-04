package org.example.restapplication.servlet.mapper;

import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.dto.OrderDto;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderComponentMapperImplTest {

    private static final UUID UUID_DEFAULT = UUID.randomUUID();
    private static final String NAME_DEFAULT = "Default sushi name";
    private static final BigDecimal PRICE_DEFAULT = BigDecimal.ZERO;
    private static final String DESCRIPTION_DEFAULT = "Default sushi description";
    private static final int AMOUNT_DEFAULT = 2;

    private final OrderComponentMapper orderComponentMapper = Mappers.getMapper(OrderComponentMapper.class);

    @Test
    void toDto() {
        OrderComponent orderComponent = new OrderComponent();
        orderComponent.setAmount(AMOUNT_DEFAULT);
        orderComponent.setSushi(new Sushi(UUID_DEFAULT, NAME_DEFAULT, new SushiType(UUID_DEFAULT, NAME_DEFAULT), PRICE_DEFAULT, DESCRIPTION_DEFAULT));
        Order order = new Order(UUID_DEFAULT, Order.OrderStatus.CONFIRMED, BigDecimal.TEN);
        orderComponent.setOrder(order);

        OrderComponentDto actual = orderComponentMapper.toDto(orderComponent);

        assertEquals(AMOUNT_DEFAULT, actual.getAmount());
        assertEquals(UUID_DEFAULT, actual.getSushi().getId());
        assertEquals(NAME_DEFAULT, actual.getSushi().getName());
        assertEquals(UUID_DEFAULT, actual.getSushi().getType().getId());
        assertEquals(NAME_DEFAULT, actual.getSushi().getType().getName());
        assertEquals(PRICE_DEFAULT, actual.getSushi().getPrice());
        assertEquals(DESCRIPTION_DEFAULT, actual.getSushi().getDescription());
        assertEquals(UUID_DEFAULT, actual.getOrder().getId());
        assertEquals(Order.OrderStatus.CONFIRMED, actual.getOrder().getStatus());
        assertEquals(BigDecimal.TEN, actual.getOrder().getTotalPrice());
    }

    @Test
    void toDtoNull() {
        assertNull(orderComponentMapper.toDto(null));
    }

    @Test
    void toOrderComponent() {
        OrderComponentDto orderComponentDto = new OrderComponentDto();
        orderComponentDto.setAmount(AMOUNT_DEFAULT);
        orderComponentDto.setSushi(new SushiDto(UUID_DEFAULT, NAME_DEFAULT, new SushiTypeDto(UUID_DEFAULT, NAME_DEFAULT), PRICE_DEFAULT, DESCRIPTION_DEFAULT));
        OrderDto orderDto = new OrderDto(UUID_DEFAULT, Order.OrderStatus.CONFIRMED, BigDecimal.TEN, null);
        orderComponentDto.setOrder(orderDto);

        OrderComponent actual = orderComponentMapper.toOrderComponent(orderComponentDto);

        assertEquals(AMOUNT_DEFAULT, actual.getAmount());
        assertEquals(UUID_DEFAULT, actual.getSushi().getId());
        assertEquals(NAME_DEFAULT, actual.getSushi().getName());
        assertEquals(UUID_DEFAULT, actual.getSushi().getType().getId());
        assertEquals(NAME_DEFAULT, actual.getSushi().getType().getName());
        assertEquals(PRICE_DEFAULT, actual.getSushi().getPrice());
        assertEquals(DESCRIPTION_DEFAULT, actual.getSushi().getDescription());
        assertEquals(UUID_DEFAULT, actual.getOrder().getId());
        assertEquals(Order.OrderStatus.CONFIRMED, actual.getOrder().getStatus());
        assertEquals(BigDecimal.TEN, actual.getOrder().getTotalPrice());
    }

    @Test
    void toOrderComponentNull() {
        assertNull(orderComponentMapper.toOrderComponent(null));
    }
}
