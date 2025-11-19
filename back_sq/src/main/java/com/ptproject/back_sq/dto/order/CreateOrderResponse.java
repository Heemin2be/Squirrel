// CreateOrderResponse.java
package com.ptproject.back_sq.dto.order;

import com.ptproject.back_sq.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CreateOrderResponse {

    private Long orderId;
    private int tableNumber;
    private OrderStatus status;
    private int totalAmount;
    private LocalDateTime orderTime;
}
