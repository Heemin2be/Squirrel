package com.ptproject.back_sq.dto.payment;

import com.ptproject.back_sq.entity.order.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreatePaymentResponse {

    private Long paymentId;
    private Long orderId;
    private PaymentMethod method;
    private int totalAmount;   // 실제 결제 금액
    private int paidAmount;    // 손님이 낸 돈
    private int change;        // 거스름돈
    private LocalDateTime paymentTime;
}
