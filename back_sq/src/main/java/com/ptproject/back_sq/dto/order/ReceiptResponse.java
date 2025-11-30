package com.ptproject.back_sq.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReceiptResponse {

    private Long orderId;
    private String tableNumber;
    private LocalDateTime orderTime;
    private LocalDateTime paymentTime;
    private String paymentMethod;
    private int totalAmount;
    private int paidAmount;
    private int changeAmount;
    private List<ReceiptItem> items;

    @Getter
    @AllArgsConstructor
    public static class ReceiptItem {
        private String menuName;
        private int quantity;
        private int price;
        private int totalPrice;
    }
}
