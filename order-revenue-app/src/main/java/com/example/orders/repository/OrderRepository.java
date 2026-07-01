package com.example.orders.repository;

import com.example.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing orders.
 * Leverages Spring Data JPA for persistence operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Spring Data JPA automatically provides common CRUD operations
}
