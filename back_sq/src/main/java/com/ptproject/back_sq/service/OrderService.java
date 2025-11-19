package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.order.CreateOrderRequest;
import com.ptproject.back_sq.dto.order.CreateOrderResponse;
import com.ptproject.back_sq.dto.order.OrderSummaryResponse;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.entity.order.*;
import com.ptproject.back_sq.repository.MenuRepository;
import com.ptproject.back_sq.repository.OrderRepository;
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

        if (status != null && date != null) {
            // 둘 다 조건 주고 싶은 경우
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            orders = orderRepository.findByStatusAndOrderTimeBetween(status, start, end);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);
            orders = orderRepository.findByOrderTimeBetween(start, end);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream()
                .map(order -> {
                    int totalAmount = order.getItems().stream()
                            .mapToInt(item -> item.getOrderedPrice() * item.getQuantity())
                            .sum();

                    return OrderSummaryResponse.builder()
                            .orderId(order.getId())
                            .tableNumber(order.getStoreTable().getTableNumber())
                            .status(order.getStatus())
                            .totalAmount(totalAmount)
                            .orderTime(order.getOrderTime())
                            .build();
                })
                .toList();
    }
}
