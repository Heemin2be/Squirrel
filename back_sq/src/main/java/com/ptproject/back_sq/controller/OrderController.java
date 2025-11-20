package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.order.CreateOrderRequest;
import com.ptproject.back_sq.dto.order.CreateOrderResponse;
import com.ptproject.back_sq.dto.order.OrderSummaryResponse;
import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.dto.payment.PaymentSummaryResponse;
import com.ptproject.back_sq.entity.order.OrderStatus;
import com.ptproject.back_sq.service.OrderService;
import com.ptproject.back_sq.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    // 👉 주문 생성 (키오스크)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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

    @GetMapping("/{orderId}")
    public CreateOrderResponse getOrder(@PathVariable Long orderId){
        return orderService.getOrder(orderId);
    }

    // 👉 결제 처리 (POS에서 사용)
    @PostMapping("/{orderId}/payment")
    public CreatePaymentResponse createPayment(
            @PathVariable Long orderId,
            @RequestBody CreatePaymentRequest request
    ) {
        return paymentService.createPayment(orderId, request);
    }
    
    // 👉 결제 취소 (POS에서 사용)
    @PostMapping("/{orderId}/cancel")
    public PaymentSummaryResponse cancelPayment(@PathVariable Long orderId) {
        return paymentService.cancelPayment(orderId);
    }


}
