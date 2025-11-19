// CreateOrderResponse.java
package com.ptproject.back_sq.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {
    private Long orderId;
    private int totalAmount;
}
