package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.order.CreateOrderRequest;
import com.ptproject.back_sq.dto.order.CreateOrderResponse;
import com.ptproject.back_sq.dto.order.OrderSummaryResponse;
import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.entity.order.*;
import com.ptproject.back_sq.repository.MenuRepository;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.repository.PaymentRepository;
import com.ptproject.back_sq.repository.StoreTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final StoreTableRepository storeTableRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // 👉 주문 생성 (키오스크에서 호출)
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        // 1) 테이블 조회
        StoreTable table = storeTableRepository.findById(request.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("테이블을 찾을 수 없습니다. id=" + request.getTableId()));

        // 2) 주문 엔티티 생성 (status=WAITING, orderTime=now)
        Order order = new Order(table);
        int totalAmount = 0;

        // 3) 주문 항목 추가
        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Menu menu = menuRepository.findById(itemReq.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다. id=" + itemReq.getMenuId()));

            // 🔹 품절 체크 (Menu 엔티티에 맞게 메서드명만 맞추면 됨)
            if (menu.isSoldOut()) {
                throw new IllegalStateException("품절된 메뉴입니다. id=" + menu.getId());
            }

            OrderItem orderItem = new OrderItem(menu, itemReq.getQuantity());
            order.addItem(orderItem);

            totalAmount += orderItem.getOrderedPrice() * itemReq.getQuantity();
        }

        // 4) 테이블 상태를 사용 중으로 변경
        table.occupy();
        // storeTableRepository.save(table); // 영속 상태라 생략해도 됨

        // 5) 주문 저장
        Order saved = orderRepository.save(order);

        // 6) 응답 DTO 생성
        return CreateOrderResponse.builder()
                .orderId(saved.getId())
                .tableNumber(saved.getStoreTable().getTableNumber())
                .status(saved.getStatus())
                .totalAmount(totalAmount)
                .orderTime(saved.getOrderTime())
                .build();
    }

    // 👉 POS 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getOrders(OrderStatus status, LocalDate date) {

        List<Order> orders;

        //status + data
        if (status != null && date != null) {
            // 둘 다 조건 주고 싶은 경우
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            orders = orderRepository.findByStatusAndOrderTimeBetween(status, start, end);
        }
        //status만
        else if (status != null) {
            orders = orderRepository.findByStatus(status);
        }
        //data만
        else if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            orders = orderRepository.findByOrderTimeBetween(start, end);
        }
        //둘 다 없는 경우
        else {
            orders = orderRepository.findAllByOrderByOrderTimeDesc();
        }

        return orders.stream()
                .map(OrderSummaryResponse::from)
                .toList();

    }
    //주문 단건 조회 (POS)
    @Transactional(readOnly = true)
    public CreateOrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. id=" + orderId));

        int totalAmount = order.getItems().stream()
                .mapToInt(item -> item.getOrderedPrice() * item.getQuantity())
                .sum();

        return CreateOrderResponse.builder()
                .orderId(order.getId())
                .tableNumber(order.getStoreTable().getTableNumber())
                .status(order.getStatus())
                .totalAmount(totalAmount)
                .orderTime(order.getOrderTime())
                .build();
    }
    // 👉 결제 처리 (POS에서 호출)
    public CreatePaymentResponse createPayment(Long orderId, CreatePaymentRequest request) {

        // 1) 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. id=" + orderId));

        // 2) 주문 상태 검증
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("취소된 주문은 결제할 수 없습니다. id=" + orderId);
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("이미 결제 완료된 주문입니다. id=" + orderId);
        }

        // 3) 실제 주문 금액 계산
        int totalAmount = order.getItems().stream()
                .mapToInt(item -> item.getOrderedPrice() * item.getQuantity())
                .sum();

        int paidAmount = request.getPaidAmount();

        // 🔹 4) 결제 수단별 검증 로직 분리
        if (request.getMethod() == PaymentMethod.CASH) {
            // 현금: 받은 금액 < 결제 금액 → 에러
            if (paidAmount < totalAmount) {
                throw new IllegalArgumentException(
                        "지불 금액이 부족합니다. 주문 금액=" + totalAmount + ", 지불 금액=" + paidAmount
                );
            }
        } else if (request.getMethod() == PaymentMethod.CARD) {
            // 카드: 정확히 맞게만 받도록 (정책에 따라 조정 가능)
            if (paidAmount != totalAmount) {
                throw new IllegalArgumentException(
                        "카드 결제 금액이 주문 금액과 일치하지 않습니다. 주문 금액=" + totalAmount + ", 지불 금액=" + paidAmount
                );
            }
        }

        int change = paidAmount - totalAmount;
        if (request.getMethod() == PaymentMethod.CARD) {
            // 카드 결제는 거스름돈 0으로 처리
            change = 0;
        }

        // 5) Payment 엔티티 생성 및 저장
        Payment payment = new Payment(totalAmount, request.getMethod());
        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);

        // 6) 주문 상태 결제 완료로 변경
        order.completePayment();      // WAITING -> PAID

        // 🔹 7) 테이블 비우기 (결제 완료 시)
        StoreTable table = order.getStoreTable();
        if (table != null) {
            table.empty();
            storeTableRepository.save(table);
        }

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


}
