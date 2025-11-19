package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.entity.employee.Employee;
import com.ptproject.back_sq.entity.employee.TimeRecord;
import com.ptproject.back_sq.repository.EmployeeRepository;
import com.ptproject.back_sq.repository.TimeRecordRepository;
import org.springframework.web.bind.annotation.*;

// EmployeeController.java
@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final TimeRecordRepository timeRecordRepository;

    public EmployeeController(EmployeeRepository employeeRepository,
                              TimeRecordRepository timeRecordRepository) {
        this.employeeRepository = employeeRepository;
        this.timeRecordRepository = timeRecordRepository;
    }

    // 직원 추가
    @PostMapping
    public Employee create(@RequestParam String name,
                           @RequestParam String role) {
        return employeeRepository.save(new Employee(name, role));
    }

    // 출근 기록
    @PostMapping("/{id}/clock-in")
    public TimeRecord clockIn(@PathVariable Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("직원 없음: " + id));
        return timeRecordRepository.save(new TimeRecord(emp));
    }

    // 퇴근 기록 (가장 최근 출근 기록에 clockOut)
    @PostMapping("/{id}/clock-out")
    public TimeRecord clockOut(@PathVariable Long id) {
        return timeRecordRepository.findByEmployeeId(id).stream()
                .filter(tr -> tr.getClockOut() == null)
                .reduce((first, second) -> second) // 마지막 것
                .map(tr -> {
                    tr.clockOut();
                    return tr;
                })
                .orElseThrow(() -> new IllegalStateException("출근 중인 기록이 없습니다."));
    }
}
