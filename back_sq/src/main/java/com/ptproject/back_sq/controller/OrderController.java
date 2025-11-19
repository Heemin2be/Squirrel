package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.order.CreateOrderRequest;
import com.ptproject.back_sq.dto.order.CreateOrderResponse;
import com.ptproject.back_sq.dto.order.OrderSummaryResponse;
import com.ptproject.back_sq.entity.order.OrderStatus;
import com.ptproject.back_sq.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    // 👉 주문 생성 (키오스크)
    @PostMapping
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    // 👉 주문 목록 조회 (POS)
    @GetMapping
    public List<OrderSummaryResponse> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
            ) {
        return orderService.getOrders(status, date);
    }
}
