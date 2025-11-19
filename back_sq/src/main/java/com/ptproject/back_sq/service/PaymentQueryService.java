package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.payment.PaymentSummaryResponse;
import com.ptproject.back_sq.entity.order.Payment;
import com.ptproject.back_sq.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<PaymentSummaryResponse> getPaymentsByDate(LocalDate date){
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        //취소된 건까지 다 보고 싶은 경우
        List<Payment> payments = paymentRepository.findByPaymentTimeBetween(start,end);

        return payments.stream()
                .map(PaymentSummaryResponse::from)
                .toList();
    }
}
