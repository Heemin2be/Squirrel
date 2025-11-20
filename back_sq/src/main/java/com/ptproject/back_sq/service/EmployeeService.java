package com.ptproject.back_sq.service;

import com.ptproject.back_sq.dto.employee.CreateEmployeeRequest;
import com.ptproject.back_sq.dto.employee.EmployeeResponse;
import com.ptproject.back_sq.dto.employee.UpdateEmployeeWageRequest;
import com.ptproject.back_sq.entity.employee.Employee;
import com.ptproject.back_sq.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    public EmployeeResponse createEmployee(CreateEmployeeRequest req) {
        String encodedPin = passwordEncoder.encode(req.getPin());

        Employee employee = new Employee(
                req.getName(),
                encodedPin,
                req.getHourlyWage(),
                req.getRole()
        );

        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeResponse updateWage(Long id, UpdateEmployeeWageRequest req) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("직원을 찾을 수 없습니다. id=" + id));

        employee.changeHourlyWage(req.getHourlyWage());
        return EmployeeResponse.from(employee);
    }
}
