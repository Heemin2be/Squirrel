package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.order.Order;
import com.ptproject.back_sq.entity.order.OrderStatus;
import com.ptproject.back_sq.entity.order.StoreTable;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByOrderTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByStatusAndOrderTimeBetween(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
    List<Order> findAllByOrderByOrderTimeDesc();
    List<Order> findByStoreTableAndStatus(StoreTable storeTable, OrderStatus status);
}

