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
    private String tableNumber;
    private OrderStatus status;
    private int totalPrice;
    private LocalDateTime orderTime;

    public static OrderSummaryResponse from(Order order){
        int amount = order.getPayment() != null
                ? order.getPayment().getTotalAmount()
                : order.calculateTotalAmount();
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .tableNumber(String.valueOf(order.getStoreTable().getTableNumber()))
                .status(order.getStatus())
                .totalPrice(amount)
                .orderTime(order.getOrderTime())
                .build();
    }
}
