package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.employee.CreateEmployeeRequest;
import com.ptproject.back_sq.dto.employee.EmployeeResponse;
import com.ptproject.back_sq.dto.employee.UpdateEmployeeWageRequest;
import com.ptproject.back_sq.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeResponse> getEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @PatchMapping("/{id}/wage")
    public EmployeeResponse updateWage(@PathVariable Long id,
                                       @RequestBody UpdateEmployeeWageRequest request) {
        return employeeService.updateWage(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}
