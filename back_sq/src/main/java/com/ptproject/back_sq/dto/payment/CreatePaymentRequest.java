// CreatePaymentRequest.java
package com.ptproject.back_sq.dto.payment;

import com.ptproject.back_sq.entity.order.PaymentMethod;
import lombok.Getter;

@Getter
public class CreatePaymentRequest {
    private Long orderId;
    private PaymentMethod method; // CARD or CASH
}
