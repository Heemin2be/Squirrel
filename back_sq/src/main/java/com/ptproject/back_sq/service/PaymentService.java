// PaymentService.java
package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.entity.order.Order;
import com.ptproject.back_sq.entity.order.OrderStatus;
import com.ptproject.back_sq.entity.order.Payment;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 없음: " + request.getOrderId()));

        // 이미 결제된 주문인지 체크 (간단히 상태로만)
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }

        // 총 금액 계산
        int totalAmount = order.getOrderItems().stream()
                .mapToInt(oi -> oi.getPrice() * oi.getQuantity())
                .sum();

        Payment payment = new Payment(order, request.getMethod(), totalAmount);
        Payment saved = paymentRepository.save(payment);

        order.changeStatus(OrderStatus.PAID); // 주문 상태 변경

        return new CreatePaymentResponse(saved.getId(), totalAmount);
    }
}
