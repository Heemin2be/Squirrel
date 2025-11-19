package com.ptproject.back_sq.repository;

import com.ptproject.back_sq.entity.employee.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeRecordRepository extends JpaRepository<TimeRecord, Long> {
    List<TimeRecord> findByEmployeeId(Long employeeId);
}