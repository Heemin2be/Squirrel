package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.order.ReceiptResponse;
import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StoreTableRepository storeTableRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ReceiptResponse createPayment(Long orderId, CreatePaymentRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. id=" + orderId));

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("취소된 주문은 결제할 수 없습니다. id=" + orderId);
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제 완료된 주문입니다. id=" + orderId);
        }

        int totalAmount = order.calculateTotalAmount();
        int paidAmount = request.getPaidAmount();

        if (request.getMethod() == PaymentMethod.CASH) {
            if (paidAmount < totalAmount) {
                throw new IllegalArgumentException(
                        "지불 금액이 부족합니다. 주문 금액=" + totalAmount + ", 지불 금액=" + paidAmount
                );
            }
        } else if (request.getMethod() == PaymentMethod.CARD) {
            if (paidAmount != totalAmount) {
                throw new IllegalArgumentException(
                        "카드 결제 금액이 주문 금액과 일치하지 않습니다. 주문 금액=" + totalAmount + ", 지불 금액=" + paidAmount
                );
            }
        }

        int change = paidAmount - totalAmount;
        if (request.getMethod() == PaymentMethod.CARD) {
            change = 0;
        }

        Payment payment = new Payment(totalAmount, paidAmount, change, request.getMethod());
        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);

        order.completePayment();

        StoreTable table = order.getStoreTable();
        if (table != null) {
            table.empty();
            storeTableRepository.save(table);
        }

        OrderStatusChangedPayload statusPayload = OrderStatusChangedPayload.from(order);
        WebSocketMessage<OrderStatusChangedPayload> statusMsg =
                new WebSocketMessage<>("order-status-changed", statusPayload);
        messagingTemplate.convertAndSend("/topic/order-status", statusMsg);

        List<ReceiptResponse.ReceiptItem> items = order.getItems().stream()
                .map(item -> new ReceiptResponse.ReceiptItem(
                        item.getMenu().getName(),
                        item.getQuantity(),
                        item.getOrderedPrice(),
                        item.getOrderedPrice() * item.getQuantity()
                ))
                .toList();

        String tableNumber = order.getStoreTable() != null
                ? String.valueOf(order.getStoreTable().getTableNumber())
                : null;

        return new ReceiptResponse(
                order.getId(),
                tableNumber,
                order.getOrderTime(),
                savedPayment.getPaymentTime(),
                savedPayment.getMethod().name(),
                savedPayment.getTotalAmount(),
                savedPayment.getPaidAmount(),
                savedPayment.getChangeAmount(),
                items
        );
    }

    public PaymentSummaryResponse cancelPayment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다. id=" + orderId));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역이 존재하지 않습니다. orderId=" + orderId));

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }

        payment.cancel();
        order.refund();

        paymentRepository.save(payment);

        return PaymentSummaryResponse.from(payment);
    }

    @Transactional
    public void createPaymentForTable(Long tableId, CreatePaymentRequest request) {
        StoreTable table = storeTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("테이블을 찾을 수 없습니다. id=" + tableId));

        List<Order> pendingOrders = orderRepository.findByStoreTableAndStatus(table, OrderStatus.PENDING);

        if (pendingOrders.isEmpty()) {
            throw new IllegalStateException("결제할 주문이 없습니다.");
        }

        int totalAmount = pendingOrders.stream().mapToInt(Order::calculateTotalAmount).sum();
        int paidAmount = request.getPaidAmount();

        if (request.getMethod() == PaymentMethod.CASH && paidAmount < totalAmount) {
            throw new IllegalArgumentException("지불 금액이 부족합니다.");
        } else if (request.getMethod() == PaymentMethod.CARD && paidAmount != totalAmount) {
            throw new IllegalArgumentException("카드 결제 금액이 주문 총액과 일치하지 않습니다.");
        }

        int totalChange = paidAmount - totalAmount;

        for (int i = 0; i < pendingOrders.size(); i++) {
            Order order = pendingOrders.get(i);
            int orderTotal = order.calculateTotalAmount();
            
            int changeForThisOrder = (i == pendingOrders.size() - 1 && request.getMethod() == PaymentMethod.CASH) ? totalChange : 0;
            int paidForThisOrder = (request.getMethod() == PaymentMethod.CARD) ? orderTotal : orderTotal + changeForThisOrder;


            Payment payment = new Payment(orderTotal, paidForThisOrder, changeForThisOrder, request.getMethod());
            payment.setOrder(order);
            paymentRepository.save(payment);

            order.completePayment();

            OrderStatusChangedPayload payload = OrderStatusChangedPayload.from(order);
            messagingTemplate.convertAndSend("/topic/order-status", new WebSocketMessage<>("order-status-changed", payload));
        }

        table.empty();
        storeTableRepository.save(table);
    }
}