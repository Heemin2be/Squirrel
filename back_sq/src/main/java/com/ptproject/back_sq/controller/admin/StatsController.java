package com.ptproject.back_sq.controller.admin;

import com.ptproject.back_sq.dto.stats.DailySalesResponse;
import com.ptproject.back_sq.dto.stats.HourlyOrderCountResponse;
import com.ptproject.back_sq.dto.stats.MonthlySalesResponse;
import com.ptproject.back_sq.dto.stats.SalesStatsResponse;
import com.ptproject.back_sq.dto.stats.TopMenuResponse;
import com.ptproject.back_sq.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatsController {

    private final StatsService statsService;

    // 하루 매출
    @GetMapping("/sales/day")
    public DailySalesResponse getDailySales(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return statsService.getDailySales(date);
    }

    // 월 매출
    @GetMapping("/sales/month")
    public MonthlySalesResponse getMonthlySales(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return statsService.getMonthlySales(year, month);
    }

    // 인기 메뉴 TOP N
    @GetMapping("/top-menus")
    public List<TopMenuResponse> getTopMenus(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return statsService.getTopMenus(startDate, endDate, limit);
    }

    // 시간대별 주문량
    @GetMapping("/orders-by-hour")
    public List<HourlyOrderCountResponse> getHourlyOrders(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return statsService.getHourlyOrders(date);
    }

    // 기간 매출
    @GetMapping("/sales")
    public SalesStatsResponse getSalesBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return statsService.getSalesBetween(startDate, endDate);
    }
}
