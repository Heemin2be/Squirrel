package com.ptproject.back_sq.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TopMenuResponse {

    private Long menuId;
    private String menuName;
    private long orderCount;        // 주문된 건수 (해당 메뉴가 포함된 주문 수)
    private long totalQuantity;     // 판매 수량 합계
    private BigDecimal totalSales;  // 해당 메뉴로 발생한 매출 합계
}
