package com.ptproject.back_sq.dto.order;

import com.ptproject.back_sq.dto.order.OrderItemResponse;
import com.ptproject.back_sq.entity.order.Order;
import com.ptproject.back_sq.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CreateOrderResponse {

    private Long orderId;
    private String tableNumber;
    private OrderStatus status;
    private int totalPrice;
    private LocalDateTime orderTime;
    private List<OrderItemResponse> items;

    public static CreateOrderResponse from(Order order) {
        int totalAmount = order.calculateTotalAmount();
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        return CreateOrderResponse.builder()
                .orderId(order.getId())
                .tableNumber(String.valueOf(order.getStoreTable().getTableNumber()))
                .status(order.getStatus())
                .totalPrice(totalAmount)
                .orderTime(order.getOrderTime())
                .items(itemResponses)
                .build();
    }
}
