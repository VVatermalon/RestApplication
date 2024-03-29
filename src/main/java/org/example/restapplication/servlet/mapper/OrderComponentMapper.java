package org.example.restapplication.servlet.mapper;

import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.dto.OrderDto;
import org.example.restapplication.servlet.dto.SushiDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Mapper
public interface OrderComponentMapper {
    OrderComponentDto toDto(OrderComponent orderComponent, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
    @DoIgnore
    default OrderComponentDto toDto(OrderComponent orderComponent) {
        return toDto(orderComponent, new CycleAvoidingMappingContext());
    }

    OrderComponent toOrderComponent(OrderComponentDto dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
    @DoIgnore
    default OrderComponent toOrderComponent(OrderComponentDto dto) {
        return toOrderComponent(dto, new CycleAvoidingMappingContext());
    }
}
