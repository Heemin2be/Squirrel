package com.ptproject.back_sq.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class SalesStatsResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSales;
    private long totalOrders;
    private List<DailySalesResponse> breakdown;
}
