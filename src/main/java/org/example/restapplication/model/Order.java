package org.example.restapplication.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order extends SimpleEntity{
    public enum OrderStatus {
        IN_PROCESS, NEED_CONFIRMATION, CONFIRMED, CANCELLED;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
    private OrderStatus status;
    private BigDecimal totalPrice;
    private List<Sushi> components;

    public Order(OrderStatus status, BigDecimal totalPrice, List<Sushi> components) {
        this.status = status;
        this.totalPrice = totalPrice;
        this.components = components;
    }

    public Order(UUID id, OrderStatus status, BigDecimal totalPrice, List<Sushi> components) {
        super(id);
        this.status = status;
        this.totalPrice = totalPrice;
        this.components = components;
    }

    public Order(UUID id, OrderStatus status, BigDecimal totalPrice) {
        super(id);
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Order(UUID id, OrderStatus status) {
        super(id);
        this.status = status;
    }

    public Order() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Sushi> getComponents() {
        return components;
    }

    public void setComponents(List<Sushi> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "Order{" +
                "status=" + status +
                ", totalPrice=" + totalPrice +
                ", components=" + components +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return status == order.status && Objects.equals(totalPrice, order.totalPrice) && Objects.equals(components, order.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, totalPrice, components);
    }
}
