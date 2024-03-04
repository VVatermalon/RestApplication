package org.example.restapplication.servlet.mapper;

import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.dto.OrderDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Mapper
public interface OrderMapper {
    OrderDto toDto(Order order,
                   @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    Order toOrder(OrderDto dto,
                  @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @DoIgnore
    default OrderDto toDto(Order order) {
        return toDto(order, new CycleAvoidingMappingContext());
    }
    @DoIgnore
    default Order toOrder(OrderDto dto) {
        return toOrder(dto, new CycleAvoidingMappingContext());
    }
}
