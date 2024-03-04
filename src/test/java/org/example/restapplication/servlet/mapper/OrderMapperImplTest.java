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
    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void toDto() {
        List<Sushi> components = new ArrayList<>();
        Sushi sushi = new Sushi(UUID_DEFAULT, NAME_DEFAULT, new SushiType(UUID_DEFAULT, NAME_DEFAULT), PRICE_DEFAULT, DESCRIPTION_DEFAULT);
        components.add(sushi);
        Order order = new Order(UUID_DEFAULT, Order.OrderStatus.CONFIRMED, BigDecimal.TEN, components);
        sushi.setOrders(List.of(order));

        OrderDto actual = orderMapper.toDto(order);

        assertEquals(UUID_DEFAULT, actual.getId());
        assertEquals(Order.OrderStatus.CONFIRMED, actual.getStatus());
        assertEquals(BigDecimal.TEN, actual.getTotalPrice());
        assertEquals(1, actual.getComponents().size());
        SushiDto sushiActual = actual.getComponents().get(0);
        assertEquals(order, sushiActual.getOrders().get(0));
        assertEquals(UUID_DEFAULT, sushiActual.getId());
        assertEquals(NAME_DEFAULT, sushiActual.getName());
        assertEquals(UUID_DEFAULT, sushiActual.getType().getId());
        assertEquals(NAME_DEFAULT, sushiActual.getType().getName());
        assertEquals(PRICE_DEFAULT, sushiActual.getPrice());
        assertEquals(DESCRIPTION_DEFAULT, sushiActual.getDescription());
    }

    @Test
    void toDtoNull() {
        assertNull(orderMapper.toDto(null));
    }

    @Test
    void toOrder() {
        List<SushiDto> componentDtos = new ArrayList<>();
        SushiDto sushiDto = new SushiDto(UUID_DEFAULT, NAME_DEFAULT, new SushiTypeDto(UUID_DEFAULT, NAME_DEFAULT), PRICE_DEFAULT, DESCRIPTION_DEFAULT);
        componentDtos.add(sushiDto);
        OrderDto orderDto = new OrderDto(UUID_DEFAULT, Order.OrderStatus.CONFIRMED, BigDecimal.TEN, componentDtos);

        Order actual = orderMapper.toOrder(orderDto);

        assertEquals(UUID_DEFAULT, actual.getId());
        assertEquals(Order.OrderStatus.CONFIRMED, actual.getStatus());
        assertEquals(BigDecimal.TEN, actual.getTotalPrice());
        assertEquals(1, actual.getComponents().size());
        Sushi actualSushi = actual.getComponents().get(0);
        assertEquals(UUID_DEFAULT, actualSushi.getId());
        assertEquals(NAME_DEFAULT, actualSushi.getName());
        assertEquals(UUID_DEFAULT, actualSushi.getType().getId());
        assertEquals(NAME_DEFAULT, actualSushi.getType().getName());
        assertEquals(PRICE_DEFAULT, actualSushi.getPrice());
        assertEquals(DESCRIPTION_DEFAULT, actualSushi.getDescription());
    }

    @Test
    void toOrderNull() {
        assertNull(orderMapper.toOrder(null));
    }
}
