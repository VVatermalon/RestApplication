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

class OrderMapperImplTest {

    private static final UUID UUID_DEFAULT = UUID.randomUUID();
    private static final String NAME_DEFAULT = "Default sushi name";
    private static final BigDecimal PRICE_DEFAULT = BigDecimal.ZERO;
    private static final String DESCRIPTION_DEFAULT = "Default sushi description";
    private static final int AMOUNT_DEFAULT = 2;

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void toDto() {
        List<OrderComponent> components = new ArrayList<>();
        OrderComponent orderComponent = new OrderComponent();
        orderComponent.setAmount(AMOUNT_DEFAULT);
        orderComponent.setSushi(new Sushi(UUID_DEFAULT, NAME_DEFAULT, new SushiType(UUID_DEFAULT, NAME_DEFAULT), PRICE_DEFAULT, DESCRIPTION_DEFAULT));
        components.add(orderComponent);
        Order order = new Order(UUID_DEFAULT, Order.OrderStatus.CONFIRMED, BigDecimal.TEN, components);
        orderComponent.setOrder(order);

        OrderDto actual = orderMapper.toDto(order);

        assertEquals(UUID_DEFAULT, actual.getId());
        assertEquals(Order.OrderStatus.CONFIRMED, actual.getStatus());
        assertEquals(BigDecimal.TEN, actual.getTotalPrice());
        assertEquals(1, actual.getComponents().size());
        OrderComponentDto actualComponent = actual.getComponents().get(0);
        assertEquals(actual, actualComponent.getOrder());
        assertEquals(AMOUNT_DEFAULT, actualComponent.getAmount());
        assertEquals(UUID_DEFAULT, actualComponent.getSushi().getId());
        assertEquals(NAME_DEFAULT, actualComponent.getSushi().getName());
        assertEquals(UUID_DEFAULT, actualComponent.getSushi().getType().getId());
        assertEquals(NAME_DEFAULT, actualComponent.getSushi().getType().getName());
        assertEquals(PRICE_DEFAULT, actualComponent.getSushi().getPrice());
        assertEquals(DESCRIPTION_DEFAULT, actualComponent.getSushi().getDescription());
    }

    @Test
    void toDtoNull() {
        assertNull(orderMapper.toDto(null));
    }

    @Test
    void toOrder() {
        List<OrderComponentDto> componentDtos = new ArrayList<>();
        OrderComponentDto orderComponentDto = new OrderComponentDto();
        orderComponentDto.setAmount(AMOUNT_DEFAULT);
        orderComponentDto.setSushi(new SushiDto(UUID_DEFAULT, NAME_DEFAULT, new SushiTypeDto(UUID_DEFAULT, NAME_DEFAULT), PRICE_DEFAULT, DESCRIPTION_DEFAULT));
        componentDtos.add(orderComponentDto);
        OrderDto orderDto = new OrderDto(UUID_DEFAULT, Order.OrderStatus.CONFIRMED, BigDecimal.TEN, componentDtos);

        Order actual = orderMapper.toOrder(orderDto);

        assertEquals(UUID_DEFAULT, actual.getId());
        assertEquals(Order.OrderStatus.CONFIRMED, actual.getStatus());
        assertEquals(BigDecimal.TEN, actual.getTotalPrice());
        assertEquals(1, actual.getComponents().size());
        OrderComponent actualComponent = actual.getComponents().get(0);
        assertEquals(AMOUNT_DEFAULT, actualComponent.getAmount());
        assertEquals(UUID_DEFAULT, actualComponent.getSushi().getId());
        assertEquals(NAME_DEFAULT, actualComponent.getSushi().getName());
        assertEquals(UUID_DEFAULT, actualComponent.getSushi().getType().getId());
        assertEquals(NAME_DEFAULT, actualComponent.getSushi().getType().getName());
        assertEquals(PRICE_DEFAULT, actualComponent.getSushi().getPrice());
        assertEquals(DESCRIPTION_DEFAULT, actualComponent.getSushi().getDescription());
    }

    @Test
    void toOrderNull() {
        assertNull(orderMapper.toOrder(null));
    }
}
