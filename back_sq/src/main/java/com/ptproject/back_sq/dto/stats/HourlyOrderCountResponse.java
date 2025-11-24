package com.ptproject.back_sq.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyOrderCountResponse {

    private int hour;           // 0 ~ 23
    private long orderCount;    // 해당 시간대 주문 수
}
