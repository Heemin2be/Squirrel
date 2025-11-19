package com.ptproject.back_sq.dto.payment;

import com.ptproject.back_sq.entity.order.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {
    private PaymentMethod method;
    private int amount;
}
