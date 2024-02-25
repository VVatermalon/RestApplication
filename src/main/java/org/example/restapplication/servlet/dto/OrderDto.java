package org.example.restapplication.servlet.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class OrderDto {
    private UUID id;
    private Order.OrderStatus status;
    private BigDecimal totalPrice;
    @JsonManagedReference
    private List<OrderComponentDto> components;

    public OrderDto(UUID id, Order.OrderStatus status, BigDecimal totalPrice, List<OrderComponentDto> components) {
        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.components = components;
    }
    public OrderDto(UUID id, Order.OrderStatus status) {
        this.id = id;
        this.status = status;
    }
    public OrderDto() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderComponentDto> getComponents() {
        return components;
    }

    public void setComponents(List<OrderComponentDto> components) {
        this.components = components;
    }
}
