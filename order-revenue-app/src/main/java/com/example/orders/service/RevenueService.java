package com.example.orders.service;

import com.example.orders.model.CustomerType;
import com.example.orders.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Part 1 - Java Core.
 * Calculates monthly revenue from a list of orders.
 *
 * Rules:
 *  - Group revenue by YearMonth (derived from Order#getOrderDate)
 *  - PREMIUM customers get a 10% discount applied to their order amount
 *  - Orders with a null or negative amount are ignored entirely
 *  - Implemented with Java 8+ Streams rather than manual loops
 */
@Service
public class RevenueService {

    private static final BigDecimal PREMIUM_DISCOUNT_RATE = new BigDecimal("0.10");

    /**
     * Returns total revenue per calendar month, sorted chronologically.
     * Uses a LinkedHashMap-backed TreeMap ordering via Comparator on collect,
     * so callers iterating the result see months in ascending order.
     */
    public Map<YearMonth, BigDecimal> calculateMonthlyRevenue(List<Order> orders) {
        if (orders == null) {
            return Map.of();
        }

        return orders.stream()
                .filter(this::isValidOrder)
                .collect(Collectors.groupingBy(
                        order -> YearMonth.from(order.getOrderDate()),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                this::netAmount,
                                BigDecimal::add
                        )
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                ));
    }

    /**
     * Orders with a null or negative amount are excluded from revenue entirely.
     * Zero-amount orders are kept (they are valid, just contribute nothing).
     */
    private boolean isValidOrder(Order order) {
        return order != null
                && order.getAmount() != null
                && order.getOrderDate() != null
                && order.getAmount().compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Applies the 10% PREMIUM discount where applicable.
     * Rounded to 2 decimal places (currency-safe) using HALF_UP.
     */
    private BigDecimal netAmount(Order order) {
        BigDecimal amount = order.getAmount();
        if (order.getCustomerType() == CustomerType.PREMIUM) {
            BigDecimal discount = amount.multiply(PREMIUM_DISCOUNT_RATE);
            amount = amount.subtract(discount);
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
