package com.example.orders.service;

import com.example.orders.dto.OrderRequest;
import com.example.orders.exception.OrderNotFoundException;
import com.example.orders.model.Order;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setCustomerType(request.getCustomerType());
        order.setAmount(request.getAmount());
        order.setOrderDate(request.getOrderDate());
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    /**
     * GET /orders?month=YYYY-MM
     * If month is null, returns all orders.
     */
    public List<Order> getOrders(YearMonth month) {
        List<Order> all = orderRepository.findAll();
        if (month == null) {
            return all;
        }
        return all.stream()
                .filter(o -> o.getOrderDate() != null && YearMonth.from(o.getOrderDate()).equals(month))
                .collect(Collectors.toList());
    }
}
