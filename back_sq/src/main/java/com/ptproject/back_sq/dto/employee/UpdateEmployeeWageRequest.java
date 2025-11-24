package com.ptproject.back_sq.dto.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 직원 시급 수정 요청에 사용하는 DTO
 * JSON 예시:
 * {
 *   "hourlyWage": 13000
 * }
 */
@Getter
@NoArgsConstructor
public class UpdateEmployeeWageRequest {

    private BigDecimal hourlyWage;
}
