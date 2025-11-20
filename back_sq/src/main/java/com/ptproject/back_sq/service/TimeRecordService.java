package com.ptproject.back_sq.service;

import com.ptproject.back_sq.entity.employee.Employee;
import com.ptproject.back_sq.entity.employee.TimeRecord;
import com.ptproject.back_sq.repository.EmployeeRepository;
import com.ptproject.back_sq.repository.TimeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimeRecordService {

    private final TimeRecordRepository timeRecordRepository;
    private final EmployeeRepository employeeRepository;

    public void clockIn(Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("직원 없음: " + employeeId));

        // 이미 출근 중인지 체크 (마지막 기록에 clockOut이 비어 있으면 출근 중)
        timeRecordRepository.findByEmployeeIdOrderByClockInDesc(employeeId)
                .stream()
                .filter(tr -> tr.getClockOut() == null)
                .findFirst()
                .ifPresent(tr -> {
                    throw new IllegalStateException("이미 출근 중입니다.");
                });

        timeRecordRepository.save(new TimeRecord(emp));
    }

    public void clockOut(Long employeeId) {
        TimeRecord last = timeRecordRepository
                .findByEmployeeIdOrderByClockInDesc(employeeId)
                .stream()
                .filter(tr -> tr.getClockOut() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("출근 중인 기록이 없습니다."));

        last.clockOut();
        timeRecordRepository.save(last);
    }
}
