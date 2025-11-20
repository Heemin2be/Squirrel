package com.ptproject.back_sq.controller;

import com.ptproject.back_sq.service.TimeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/time-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TimeRecordController {

    private final TimeRecordService timeRecordService;

    // 내 계정으로 출근
    @PostMapping("/clock-in")
    public void clockIn(Authentication authentication) {
        Long employeeId = Long.parseLong(authentication.getName()); // subject = employeeId
        timeRecordService.clockIn(employeeId);
    }

    // 내 계정으로 퇴근
    @PostMapping("/clock-out")
    public void clockOut(Authentication authentication) {
        Long employeeId = Long.parseLong(authentication.getName());
        timeRecordService.clockOut(employeeId);
    }
}
