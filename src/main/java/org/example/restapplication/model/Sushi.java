package org.example.restapplication.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Sushi extends SimpleEntity {
    private String name;
    private SushiType type;
    private BigDecimal price;
    private String description;
    private List<Order> orders;

    public Sushi(String name, SushiType type, BigDecimal price, String description, List<Order> orders) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
    }

    public Sushi(UUID id, String name, SushiType type, BigDecimal price, String description, List<Order> orders) {
        super(id);
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
    }
    public Sushi(UUID id, String name, SushiType type, BigDecimal price, String description) {
        super(id);
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
    }

    public Sushi() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SushiType getType() {
        return type;
    }

    public void setType(SushiType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Sushi{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", orders=" + orders +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sushi sushi)) return false;
        return Objects.equals(name, sushi.name) && Objects.equals(type, sushi.type) && Objects.equals(price, sushi.price) && Objects.equals(description, sushi.description) && Objects.equals(orders, sushi.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, price, description, orders);
    }
}
