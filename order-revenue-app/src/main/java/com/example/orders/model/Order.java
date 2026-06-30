package com.example.orders.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain model representing a single order.
 * Amount uses BigDecimal to avoid floating point rounding issues with currency.
 */
public class Order {

    private Long id;
    private String customerId;
    private CustomerType customerType;
    private BigDecimal amount;
    private LocalDate orderDate;

    public Order() {
    }

    public Order(Long id, String customerId, CustomerType customerType, BigDecimal amount, LocalDate orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.customerType = customerType;
        this.amount = amount;
        this.orderDate = orderDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", customerType=" + customerType +
                ", amount=" + amount +
                ", orderDate=" + orderDate +
                '}';
    }
}
