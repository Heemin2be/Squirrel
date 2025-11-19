// CreateOrderRequest.java
package com.ptproject.back_sq.dto.order;

import lombok.Getter;
import java.util.List;

@Getter
public class CreateOrderRequest {

    private int tableNumber;          // 5번 테이블
    private List<Item> items;         // 메뉴 + 수량 목록

    @Getter
    public static class Item {
        private Long menuId;
        private int quantity;
    }
}
