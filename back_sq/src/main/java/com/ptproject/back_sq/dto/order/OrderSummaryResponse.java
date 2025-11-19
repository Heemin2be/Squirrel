package com.ptproject.back_sq.dto.order;


import com.ptproject.back_sq.entity.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderSummaryResponse {
    private Long orderId;
    private int tableNumber;
    private OrderStatus status;
    private int totalAmount;
    private LocalDateTime orderTime;
}
