package com.ptproject.back_sq.dto.websocket;

import com.ptproject.back_sq.entity.order.Order;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewOrderPayload {
    private Long orderId;
    private String status;
    private String tableNumber;
    private LocalDateTime orderTime;
    private Integer totalPrice;

    public static NewOrderPayload from(Order order) {
        NewOrderPayload p = new NewOrderPayload();
        p.setOrderId(order.getId());
        p.setStatus(order.getStatus().name());
        p.setTableNumber(order.getStoreTable() != null
                ? String.valueOf(order.getStoreTable().getTableNumber())
                : null);
        p.setOrderTime(order.getOrderTime());
        p.setTotalPrice(order.calculateTotalAmount());
        return p;
    }
}
