// OrderController.java
package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.order.CreateOrderRequest;
import com.ptproject.back_sq.dto.order.CreateOrderResponse;
import com.ptproject.back_sq.entity.order.Order;
import com.ptproject.back_sq.entity.order.OrderStatus;
import com.ptproject.back_sq.repository.OrderRepository;
import com.ptproject.back_sq.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService,
                           OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    // 주문 생성 (키오스크에서 사용)
    @PostMapping
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    // 상태별 주문 목록 조회 (POS에서 사용)
    @GetMapping
    public List<Order> getOrders(@RequestParam(required = false) OrderStatus status) {
        if (status == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findByStatus(status);
    }

    // 주문 상세 조회
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음: " + id));
    }
}
