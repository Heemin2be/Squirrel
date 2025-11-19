package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}