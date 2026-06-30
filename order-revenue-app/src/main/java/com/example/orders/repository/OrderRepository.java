package com.example.orders.repository;

import com.example.orders.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory order store.
 * ConcurrentHashMap + AtomicLong keep this safe under concurrent requests
 * without needing an external database for this exercise.
 */
@Repository
public class OrderRepository {

    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public Order save(Order order) {
        long id = idGenerator.incrementAndGet();
        order.setId(id);
        store.put(id, order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Collection<Order> findAll() {
        return store.values();
    }
}
