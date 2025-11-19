package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/{orderId}/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    // 👉 결제 요청 (POS)
    @PostMapping
    public CreatePaymentResponse pay(
            @PathVariable Long orderId,
            @RequestBody CreatePaymentRequest request
    ) {
        return paymentService.createPayment(orderId, request);
    }
}
