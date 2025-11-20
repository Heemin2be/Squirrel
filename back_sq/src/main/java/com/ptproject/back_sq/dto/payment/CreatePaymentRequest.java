package com.ptproject.back_sq.dto.payment;

import com.ptproject.back_sq.entity.order.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePaymentRequest {
    private Long orderId;
    private PaymentMethod method;  // CARD / CASH
    private int paidAmount;        // 손님이 낸 금액
}
