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

        StoreTable table = storeTableRepository.findById(request.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("테이블을 찾을 수 없습니다. id=" + request.getTableId()));

        Order order = new Order(table);
        int totalAmount = 0;

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Menu menu = menuRepository.findById(itemReq.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다. id=" + itemReq.getMenuId()));

            OrderItem orderItem = new OrderItem(menu, itemReq.getQuantity());
            order.addItem(orderItem);

            totalAmount += orderItem.getOrderedPrice() * itemReq.getQuantity();
        }

        // 테이블 상태를 사용 중으로
        table.occupy();

        storeTableRepository.save(table);
        Order saved = orderRepository.save(order);

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

        if (status != null) {
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
