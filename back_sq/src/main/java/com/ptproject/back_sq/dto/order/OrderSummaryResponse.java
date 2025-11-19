package com.ptproject.back_sq.dto.order;


import com.ptproject.back_sq.entity.order.Order;
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

    public static OrderSummaryResponse from(Order order){
        int amount = 0;
        if (order.getPayment() != null){
            amount = order.getPayment().getTotalAmount();
        }
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .tableNumber(order.getStoreTable().getTableNumber())
                .status(order.getStatus())
                .totalAmount(amount)
                .orderTime(order.getOrderTime())
                .build();
    }
}
