package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.dto.payment.PaymentSummaryResponse;
import com.ptproject.back_sq.dto.websocket.OrderStatusChangedPayload;
import com.ptproject.back_sq.dto.websocket.WebSocketMessage;
import com.ptproject.back_sq.entity.order.*;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.repository.PaymentRepository;
import com.ptproject.back_sq.repository.StoreTableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StoreTableRepository storeTableRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 👉 결제 처리 (POS)
    public CreatePaymentResponse createPayment(Long orderId, CreatePaymentRequest request) {

        // 1) 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. id=" + orderId));

        // 2) 주문 상태 검증
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("취소된 주문은 결제할 수 없습니다. id=" + orderId);
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제 완료된 주문입니다. id=" + orderId);
        }

        // 3) 실제 주문 금액 계산
        int totalAmount = order.calculateTotalAmount();
        int paidAmount = request.getPaidAmount();

        // 4) 결제 수단별 검증
        if (request.getMethod() == PaymentMethod.CASH) {
            // 현금: 받은 금액 < 결제 금액 → 에러
            if (paidAmount < totalAmount) {
                throw new IllegalArgumentException(
                        "지불 금액이 부족합니다. 주문 금액=" + totalAmount + ", 지불 금액=" + paidAmount
                );
            }
        } else if (request.getMethod() == PaymentMethod.CARD) {
            // 카드: 정확히 맞게만
            if (paidAmount != totalAmount) {
                throw new IllegalArgumentException(
                        "카드 결제 금액이 주문 금액과 일치하지 않습니다. 주문 금액=" + totalAmount + ", 지불 금액=" + paidAmount
                );
            }
        }

        int change = paidAmount - totalAmount;
        if (request.getMethod() == PaymentMethod.CARD) {
            // 카드 결제는 거스름돈 0
            change = 0;
        }

        // 5) Payment 엔티티 생성 및 저장
        Payment payment = new Payment(totalAmount, request.getMethod());
        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);

        // 6) 주문 상태 결제 완료로 변경
        order.completePayment();  // WAITING -> PAID

        // 7) 테이블 비우기
        StoreTable table = order.getStoreTable();
        if (table != null) {
            table.empty();
            storeTableRepository.save(table);
        }

        // ⭐ WebSocket: 주문 상태 변경 알림
        OrderStatusChangedPayload statusPayload = OrderStatusChangedPayload.from(order);
        WebSocketMessage<OrderStatusChangedPayload> statusMsg =
                new WebSocketMessage<>("order-status-changed", statusPayload);
        messagingTemplate.convertAndSend("/topic/order-status", statusMsg);

        // 8) 응답 DTO 생성
        return CreatePaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .orderId(order.getId())
                .method(savedPayment.getMethod())
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .change(change)
                .paymentTime(savedPayment.getPaymentTime())
                .build();
    }

    // 👉 결제 취소 (POS)
    public PaymentSummaryResponse cancelPayment(Long orderId) {

        // 1) 주문 찾기
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. id=" + orderId));

        // 2) 주문에 연결된 결제 찾기
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역이 존재하지 않습니다. orderId=" + orderId));

        // 3) 이미 취소된 결제면 막기
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        // 4) 결제/주문 상태 변경
        payment.cancel();
        order.cancelPayment();

        paymentRepository.save(payment);
        orderRepository.save(order);

        // (필요하면 여기서도 WebSocket으로 "order-status-changed" 보내줄 수 있음)

        return PaymentSummaryResponse.from(payment);
    }
}
