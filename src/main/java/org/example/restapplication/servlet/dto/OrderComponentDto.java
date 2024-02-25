package org.example.restapplication.servlet.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderComponentDto {
    private UUID id;
    @JsonBackReference
    private OrderDto order;
    private SushiDto sushi;
    private int amount;

    public OrderComponentDto(OrderDto order, SushiDto sushi, int amount) {
        this.order = order;
        this.sushi = sushi;
        this.amount = amount;
    }

    public OrderComponentDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public SushiDto getSushi() {
        return sushi;
    }

    public void setSushi(SushiDto sushi) {
        this.sushi = sushi;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
