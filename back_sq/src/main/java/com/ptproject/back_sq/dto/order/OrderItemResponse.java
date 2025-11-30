package com.ptproject.back_sq.dto.order;

import com.ptproject.back_sq.entity.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponse {
    private Long menuId;
    private String menuName;
    private int quantity;
    private int price;

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .menuId(orderItem.getMenu().getId())
                .menuName(orderItem.getMenu().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getOrderedPrice())
                .build();
    }
}