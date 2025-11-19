package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.dto.payment.PaymentSummaryResponse;
import com.ptproject.back_sq.entity.order.*;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.repository.PaymentRepository;
import com.ptproject.back_sq.repository.StoreTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        order.completePayment();  // 상태 -> PAID

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

    // 👉 결제 취소 (POS)
    public PaymentSummaryResponse cancelPayment(Long orderId) {

        // 1) 주문 찾기
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. id=" + orderId));

        // 2) 주문에 연결된 결제 찾기
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 존재하지 않습니다. orderId=" + orderId));

        // 3) 이미 취소된 결제면 막기
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        // 4) 결제/주문 상태 변경 (엔티티 메서드 사용)
        payment.cancel();         // ← Payment 엔티티에서 방금 만든 메서드
        order.cancelPayment();    // ← Order 엔티티에서 방금 만든 메서드

        paymentRepository.save(payment);
        orderRepository.save(order);

        return PaymentSummaryResponse.from(payment);
    }
}
