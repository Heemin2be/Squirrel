package com.ptproject.back_sq.dto.employee;

import com.ptproject.back_sq.entity.employee.EmployeeRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 직원 등록(생성) 요청에 사용하는 DTO
 * JSON 예시:
 * {
 *   "name": "홍길동",
 *   "pin": "0000",
 *   "hourlyWage": 12000,
 *   "role": "ADMIN"
 * }
 */
@Getter
@NoArgsConstructor
public class CreateEmployeeRequest {

    private String name;
    private String pin;
    private BigDecimal hourlyWage;
    private EmployeeRole role;
}
