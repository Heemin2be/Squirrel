// PaymentController.java
package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.payment.CreatePaymentRequest;
import com.ptproject.back_sq.dto.payment.CreatePaymentResponse;
import com.ptproject.back_sq.entity.order.Payment;
import com.ptproject.back_sq.repository.PaymentRepository;
import com.ptproject.back_sq.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService,
                             PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public CreatePaymentResponse createPayment(@RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }
}
