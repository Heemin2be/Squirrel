package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.payment.PaymentSummaryResponse;
import com.ptproject.back_sq.service.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentQueryController {

    private final PaymentQueryService paymentQueryService;

    @GetMapping
    public List<PaymentSummaryResponse> getPaymentsByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return paymentQueryService.getPaymentsByDate(date);
    }
}

