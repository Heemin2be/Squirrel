package com.ptproject.back_sq.dto.employee;

import com.ptproject.back_sq.entity.employee.Employee;
import com.ptproject.back_sq.entity.employee.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 직원 한 명의 정보를 응답으로 내려줄 때 사용하는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String name;
    private BigDecimal hourlyWage;
    private EmployeeRole role;

    public static EmployeeResponse from(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .hourlyWage(employee.getHourlyWage())
                .role(employee.getRole())
                .build();
    }
}
