package com.ptproject.back_sq.dto.websocket;

import com.ptproject.back_sq.entity.order.Order;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewOrderPayload {
    private Long id;
    private String status;
    private Integer tableNumber;
    private LocalDateTime orderTime;
    private Integer totalAmount;

    public static NewOrderPayload from(Order order) {
        NewOrderPayload p = new NewOrderPayload();
        p.setId(order.getId());
        p.setStatus(order.getStatus().name());
        p.setTableNumber(order.getStoreTable() != null
                ? order.getStoreTable().getTableNumber()
                : null);
        p.setOrderTime(order.getOrderTime());
        p.setTotalAmount(order.calculateTotalAmount());
        return p;
    }
}
