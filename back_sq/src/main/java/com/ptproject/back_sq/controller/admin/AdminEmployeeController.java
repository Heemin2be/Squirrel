package com.ptproject.back_sq.controller.admin;

import com.ptproject.back_sq.dto.employee.CreateEmployeeRequest;
import com.ptproject.back_sq.dto.employee.EmployeeResponse;
import com.ptproject.back_sq.dto.employee.UpdateEmployeeWageRequest;
import com.ptproject.back_sq.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminEmployeeController {

    private final EmployeeService employeeService;

    // 직원 목록 조회
    @GetMapping
    public List<EmployeeResponse> getEmployees() {
        return employeeService.getAllEmployees();
    }

    // 직원 등록
    @PostMapping
    public EmployeeResponse createEmployee(@RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    // 직원 삭제
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }

    // 시급 수정
    @PatchMapping("/{id}/wage")
    public EmployeeResponse updateWage(@PathVariable Long id,
                                       @RequestBody UpdateEmployeeWageRequest request) {
        return employeeService.updateWage(id, request);
    }
}
