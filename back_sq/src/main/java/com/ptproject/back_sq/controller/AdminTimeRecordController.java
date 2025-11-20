package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.dto.employee.TimeRecordResponse;
import com.ptproject.back_sq.entity.employee.TimeRecord;
import com.ptproject.back_sq.repository.TimeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/time-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminTimeRecordController {

    private final TimeRecordRepository timeRecordRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TimeRecordResponse> getAll() {
        List<TimeRecord> records = timeRecordRepository.findAll();

        return records.stream()
                .map(tr -> new TimeRecordResponse(
                        tr.getId(),
                        tr.getEmployee().getId(),
                        tr.getEmployee().getName(),
                        tr.getClockIn(),
                        tr.getClockOut()
                ))
                .toList();
    }
}
