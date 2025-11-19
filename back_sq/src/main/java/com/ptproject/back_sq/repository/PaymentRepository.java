package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}