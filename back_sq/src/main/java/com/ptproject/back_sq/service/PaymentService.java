package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.entity.order.*;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.repository.PaymentRepository;
import com.ptproject.back_sq.repository.StoreTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StoreTableRepository storeTableRepository;

    // 👉 결제 처리 (POS)
    public CreatePaymentResponse createPayment(Long orderId, CreatePaymentRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. id=" + orderId));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제된 주문입니다.");
        }

        int totalAmount = order.getItems().stream()
                .mapToInt(item -> item.getOrderedPrice() * item.getQuantity())
                .sum();

        if (request.getPaidAmount() < totalAmount) {
            throw new IllegalArgumentException("받은 금액이 결제 금액보다 적습니다.");
        }

        int change = request.getPaidAmount() - totalAmount;

    Payment payment = new Payment(totalAmount, request.getMethod());
        order.addPayment(payment);
        order.complete();  // 상태 -> PAID

        // 테이블 비우기
        StoreTable table = order.getStoreTable();
        table.empty();
        storeTableRepository.save(table);

        paymentRepository.save(payment);
        orderRepository.save(order);

        return CreatePaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(order.getId())
                .method(payment.getMethod())
                .totalAmount(totalAmount)
                .paidAmount(request.getPaidAmount())
                .change(change)
                .paymentTime(payment.getPaymentTime())
                .build();
    }
}
