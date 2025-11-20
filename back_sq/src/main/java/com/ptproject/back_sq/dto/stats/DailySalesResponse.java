package com.ptproject.back_sq.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailySalesResponse {

    private LocalDate date;          // 날짜
    private BigDecimal totalSales;   // 총 매출 금액
    private long orderCount;         // 주문 개수
}
