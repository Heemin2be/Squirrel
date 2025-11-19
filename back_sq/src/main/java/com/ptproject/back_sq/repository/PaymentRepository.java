package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.order.Payment;
import com.ptproject.back_sq.entity.order.PaymentStatus;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Payment> findByStatusAndPaymentTimeBetween(
            PaymentStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
}