package com.example.orders.controller;

import com.example.orders.model.Order;
import com.example.orders.service.OrderService;
import com.example.orders.service.RevenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/revenue")
public class RevenueController {

    private final OrderService orderService;
    private final RevenueService revenueService;

    public RevenueController(OrderService orderService,
                             RevenueService revenueService) {
        this.orderService = orderService;
        this.revenueService = revenueService;
    }

    @GetMapping
    public ResponseEntity<Map<YearMonth, BigDecimal>> getMonthlyRevenue() {

        List<Order> orders = orderService.getOrders(null);

        Map<YearMonth, BigDecimal> revenue =
                revenueService.calculateMonthlyRevenue(orders);

        return ResponseEntity.ok(revenue);
    }
}