package com.ptproject.back_sq.dto.websocket;

import com.ptproject.back_sq.entity.order.Order;
import lombok.Data;

@Data
public class OrderStatusChangedPayload {
    private Long id;
    private String status;

    public static OrderStatusChangedPayload from(Order order) {
        OrderStatusChangedPayload p = new OrderStatusChangedPayload();
        p.setId(order.getId());
        p.setStatus(order.getStatus().name());
        return p;
    }
}
