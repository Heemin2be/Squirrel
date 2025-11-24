package com.ptproject.back_sq.dto.employee;

import com.ptproject.back_sq.entity.employee.EmployeeRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 직원 정보 전체 수정(이름, 시급, 역할 등)을 위한 DTO
 * 필요 없으면 안 써도 됨.
 */
@Getter
@NoArgsConstructor
public class UpdateEmployeeRequest {

    private String name;
    private BigDecimal hourlyWage;
    private EmployeeRole role;
}
