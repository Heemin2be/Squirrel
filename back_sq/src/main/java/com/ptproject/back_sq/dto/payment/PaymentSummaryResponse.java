package com.ptproject.back_sq.dto.payment;

import com.ptproject.back_sq.entity.order.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentSummaryResponse {
    private Long paymentId;
    private Long orderId;
    private LocalDateTime paymentTime;
    private int totalAmount;
    private String method; //"CARD","CASH"
    private String status; //"COMPLETED","CANCELED"
    private int paidAmount;
    private int change;

    public static PaymentSummaryResponse from(Payment payment) {
        return PaymentSummaryResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .totalAmount(payment.getTotalAmount())
                .method(payment.getMethod().name())
                .paymentTime(payment.getPaymentTime())
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .paidAmount(payment.getPaidAmount())
                .change(payment.getChangeAmount())
                .build();
    }
}
