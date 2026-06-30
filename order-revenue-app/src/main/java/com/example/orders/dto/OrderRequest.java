package com.example.orders.dto;

import com.example.orders.model.CustomerType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Inbound payload for POST /orders.
 * id is intentionally excluded — assigned server-side.
 */
public class OrderRequest {

    @NotNull(message = "customerId is required")
    private String customerId;

    @NotNull(message = "customerType is required")
    private CustomerType customerType;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "amount must not be negative")
    private BigDecimal amount;

    @NotNull(message = "orderDate is required")
    @PastOrPresent(message = "orderDate cannot be in the future")
    private LocalDate orderDate;

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
}
