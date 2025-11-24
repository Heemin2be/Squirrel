package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.stats.DailySalesResponse;
import com.ptproject.back_sq.dto.stats.HourlyOrderCountResponse;
import com.ptproject.back_sq.dto.stats.MonthlySalesResponse;
import com.ptproject.back_sq.dto.stats.TopMenuResponse;
import com.ptproject.back_sq.dto.stats.SalesStatsResponse;
import com.ptproject.back_sq.entity.menu.Menu;
import com.ptproject.back_sq.entity.order.Order;
import com.ptproject.back_sq.entity.order.OrderItem;
import com.ptproject.back_sq.entity.order.OrderStatus;
import com.ptproject.back_sq.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final OrderRepository orderRepository;

    /**
     * 하루 매출
     */
    public DailySalesResponse getDailySales(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Order> paidOrders = orderRepository.findByStatusAndOrderTimeBetween(
                OrderStatus.PAID, start, end);

        BigDecimal totalSales = paidOrders.stream()
                .map(this::calculateOrderTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = paidOrders.size();

        return new DailySalesResponse(date, totalSales, count);
    }

    /**
     * 월 매출 + 일별 매출
     */
    public MonthlySalesResponse getMonthlySales(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate firstDay = ym.atDay(1);
        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        List<Order> paidOrders = orderRepository.findByStatusAndOrderTimeBetween(
                OrderStatus.PAID, start, end);

        // 일자별 그룹핑
        Map<LocalDate, List<Order>> byDate = paidOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderTime().toLocalDate()));

        List<DailySalesResponse> dailyList = new ArrayList<>();
        BigDecimal monthlyTotal = BigDecimal.ZERO;
        long monthlyCount = 0L;

        for (LocalDate d : byDate.keySet()) {
            List<Order> dayOrders = byDate.get(d);

            BigDecimal dayTotal = dayOrders.stream()
                    .map(this::calculateOrderTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long dayCount = dayOrders.size();

            monthlyTotal = monthlyTotal.add(dayTotal);
            monthlyCount += dayCount;

            dailyList.add(new DailySalesResponse(d, dayTotal, dayCount));
        }

        // 날짜순 정렬
        dailyList.sort(Comparator.comparing(DailySalesResponse::getDate));

        return new MonthlySalesResponse(year, month, monthlyTotal, monthlyCount, dailyList);
    }

    /**
     * 인기 메뉴 TOP N
     */
    public List<TopMenuResponse> getTopMenus(LocalDate startDate, LocalDate endDate, int limit) {
        // 날짜 구간: startDate ~ endDate (포함)
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Order> paidOrders = orderRepository.findByStatusAndOrderTimeBetween(
                OrderStatus.PAID, start, end);

        Map<Long, MenuStat> statMap = new HashMap<>();

        for (Order order : paidOrders) {
            for (OrderItem item : order.getItems()) {
                Menu menu = item.getMenu();
                if (menu == null) continue;

                Long menuId = menu.getId();
                MenuStat stat = statMap.computeIfAbsent(menuId, id ->
                        new MenuStat(menuId, menu.getName()));

                int qty = item.getQuantity();

                // orderedPrice * quantity
                BigDecimal lineAmount =
                        BigDecimal.valueOf((long) item.getOrderedPrice() * qty);

                stat.add(qty, lineAmount);
            }
        }

        return statMap.values().stream()
                .sorted(Comparator.comparing(MenuStat::getTotalQuantity).reversed())
                .limit(limit)
                .map(stat -> new TopMenuResponse(
                        stat.getMenuId(),
                        stat.getMenuName(),
                        stat.getOrderCount(),
                        stat.getTotalQuantity(),
                        stat.getTotalSales()
                ))
                .toList();
    }

    /**
     * 시간대별 주문 수 (하루 기준)
     */
    public List<HourlyOrderCountResponse> getHourlyOrders(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Order> paidOrders = orderRepository.findByStatusAndOrderTimeBetween(
                OrderStatus.PAID, start, end);

        Map<Integer, Long> byHour = paidOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderTime().getHour(),
                        Collectors.counting()
                ));

        List<HourlyOrderCountResponse> result = new ArrayList<>();

        // 0~23시 전부 반환 (주문 없으면 0)
        for (int h = 0; h < 24; h++) {
            long cnt = byHour.getOrDefault(h, 0L);
            result.add(new HourlyOrderCountResponse(h, cnt));
        }

        return result;
    }

    /**
     * 주문 한 건의 총 금액 계산 (orderedPrice * quantity 합계)
     */
    private BigDecimal calculateOrderTotal(Order order) {
        return order.getItems().stream()
                .map(item -> BigDecimal.valueOf(
                        (long) item.getOrderedPrice() * item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 기간 매출 요약
     */
    public SalesStatsResponse getSalesBetween(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate는 startDate 이후여야 합니다.");
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime endExclusive = endDate.plusDays(1).atStartOfDay();

        List<Order> paidOrders = orderRepository.findByStatusAndOrderTimeBetween(
                OrderStatus.PAID, start, endExclusive);

        Map<LocalDate, List<Order>> byDate = paidOrders.stream()
                .collect(Collectors.groupingBy(o -> o.getOrderTime().toLocalDate()));

        List<DailySalesResponse> breakdown = byDate.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(this::calculateOrderTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    long count = entry.getValue().size();
                    return new DailySalesResponse(entry.getKey(), total, count);
                })
                .sorted(Comparator.comparing(DailySalesResponse::getDate))
                .toList();

        BigDecimal totalSales = breakdown.stream()
                .map(DailySalesResponse::getTotalSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalOrders = breakdown.stream()
                .mapToLong(DailySalesResponse::getOrderCount)
                .sum();

        return new SalesStatsResponse(startDate, endDate, totalSales, totalOrders, breakdown);
    }

    /**
     * 내부 집계용 클래스
     */
    private static class MenuStat {
        private final Long menuId;
        private final String menuName;
        private long orderCount;
        private long totalQuantity;
        private BigDecimal totalSales = BigDecimal.ZERO;

        public MenuStat(Long menuId, String menuName) {
            this.menuId = menuId;
            this.menuName = menuName;
        }

        public void add(long quantity, BigDecimal amount) {
            this.orderCount += 1;          // 이 메뉴가 포함된 orderItem 1건
            this.totalQuantity += quantity;
            this.totalSales = this.totalSales.add(amount);
        }

        public Long getMenuId() {
            return menuId;
        }

        public String getMenuName() {
            return menuName;
        }

        public long getOrderCount() {
            return orderCount;
        }

        public long getTotalQuantity() {
            return totalQuantity;
        }

        public BigDecimal getTotalSales() {
            return totalSales;
        }
    }
}
