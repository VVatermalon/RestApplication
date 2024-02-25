package org.example.restapplication.model;

import java.util.Objects;
import java.util.UUID;

public class OrderComponent extends SimpleEntity{
    private Sushi sushi;
    private Order order;
    private int amount;

    public OrderComponent(Order order, Sushi sushi, int amount) {
        this.sushi = sushi;
        this.order = order;
        this.amount = amount;
    }

    public OrderComponent() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Sushi getSushi() {
        return sushi;
    }

    public void setSushi(Sushi sushi) {
        this.sushi = sushi;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "OrderComponent{" +
                "sushi=" + sushi +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderComponent that)) return false;
        return amount == that.amount && Objects.equals(sushi, that.sushi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sushi, amount);
    }
}
