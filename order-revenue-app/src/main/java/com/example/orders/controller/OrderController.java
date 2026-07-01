package com.example.orders.controller;

import com.example.orders.dto.OrderRequest;
import com.example.orders.model.Order;
import com.example.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order and returns the saved record.
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        Order created = orderService.createOrder(request);
        URI location = URI.create("/orders/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Returns a single order by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Lists orders, optionally filtered by month.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        List<Order> orders = orderService.getOrders(month);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }
}
