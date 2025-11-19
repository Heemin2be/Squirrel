// CreatePaymentResponse.java
package com.ptproject.back_sq.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatePaymentResponse {
    private Long paymentId;
    private int amount;
}
