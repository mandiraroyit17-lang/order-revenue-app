package com.example.orders.service;

import com.example.orders.model.CustomerType;
import com.example.orders.model.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RevenueServiceTest {

    private final RevenueService revenueService = new RevenueService();

    @Test
    void groupsRevenueByYearMonth() {
        List<Order> orders = Arrays.asList(
                order(1L, CustomerType.STANDARD, "100.00", "2024-01-15"),
                order(2L, CustomerType.STANDARD, "50.00", "2024-01-20"),
                order(3L, CustomerType.STANDARD, "200.00", "2024-02-01")
        );

        Map<YearMonth, BigDecimal> result = revenueService.calculateMonthlyRevenue(orders);

        assertEquals(new BigDecimal("150.00"), result.get(YearMonth.of(2024, 1)));
        assertEquals(new BigDecimal("200.00"), result.get(YearMonth.of(2024, 2)));
    }

    @Test
    void appliesTenPercentDiscountForPremiumCustomers() {
        List<Order> orders = List.of(
                order(1L, CustomerType.PREMIUM, "100.00", "2024-01-15")
        );

        Map<YearMonth, BigDecimal> result = revenueService.calculateMonthlyRevenue(orders);

        // 100.00 - 10% = 90.00
        assertEquals(new BigDecimal("90.00"), result.get(YearMonth.of(2024, 1)));
    }

    @Test
    void doesNotDiscountStandardCustomers() {
        List<Order> orders = List.of(
                order(1L, CustomerType.STANDARD, "100.00", "2024-01-15")
        );

        Map<YearMonth, BigDecimal> result = revenueService.calculateMonthlyRevenue(orders);

        assertEquals(new BigDecimal("100.00"), result.get(YearMonth.of(2024, 1)));
    }

    @Test
    void ignoresNullAmounts() {
        Order order = order(1L, CustomerType.STANDARD, null, "2024-01-15");
        // override directly since helper requires a string amount
        order.setAmount(null);

        Map<YearMonth, BigDecimal> result = revenueService.calculateMonthlyRevenue(List.of(order));

        assertTrue(result.isEmpty());
    }

    @Test
    void ignoresNegativeAmounts() {
        List<Order> orders = List.of(
                order(1L, CustomerType.STANDARD, "-50.00", "2024-01-15"),
                order(2L, CustomerType.STANDARD, "100.00", "2024-01-15")
        );

        Map<YearMonth, BigDecimal> result = revenueService.calculateMonthlyRevenue(orders);

        // only the 100.00 order should count; -50.00 ignored entirely
        assertEquals(new BigDecimal("100.00"), result.get(YearMonth.of(2024, 1)));
    }

    @Test
    void keepsZeroAmountOrders() {
        List<Order> orders = List.of(
                order(1L, CustomerType.STANDARD, "0.00", "2024-01-15")
        );

        Map<YearMonth, BigDecimal> result = revenueService.calculateMonthlyRevenue(orders);

        assertEquals(new BigDecimal("0.00"), result.get(YearMonth.of(2024, 1)));
    }

    @Test
    void handlesNullOrderListGracefully() {
        Map<YearMonth, BigDecimal> result = revenueService.calculateMonthlyRevenue(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void resultIsSortedChronologically() {
        List<Order> orders = Arrays.asList(
                order(1L, CustomerType.STANDARD, "10.00", "2024-03-01"),
                order(2L, CustomerType.STANDARD, "10.00", "2024-01-01"),
                order(3L, CustomerType.STANDARD, "10.00", "2024-02-01")
        );

        List<YearMonth> keysInOrder = revenueService.calculateMonthlyRevenue(orders)
                .keySet().stream().toList();

        assertEquals(
                List.of(YearMonth.of(2024, 1), YearMonth.of(2024, 2), YearMonth.of(2024, 3)),
                keysInOrder
        );
    }

    private Order order(Long id, CustomerType type, String amount, String date) {
        Order o = new Order();
        o.setId(id);
        o.setCustomerId("CUST-" + id);
        o.setCustomerType(type);
        o.setAmount(amount == null ? null : new BigDecimal(amount));
        o.setOrderDate(LocalDate.parse(date));
        return o;
    }
}
