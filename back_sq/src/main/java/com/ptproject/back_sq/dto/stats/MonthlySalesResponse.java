package com.ptproject.back_sq.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class MonthlySalesResponse {

    private int year;
    private int month;
    private BigDecimal totalSales;           // 월 전체 매출
    private long orderCount;                 // 월 전체 주문 수
    private List<DailySalesResponse> days;   // 일별 상세
}
