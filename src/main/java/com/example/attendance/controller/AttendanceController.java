package com.example.attendance.controller;

import com.example.attendance.model.DailyAttendanceResult;
import com.example.attendance.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * GET /api/attendance/EMP001?date=2026-02-18
     */
    @GetMapping("/{employeeId}")
    public DailyAttendanceResult getAttendanceForDate(
            @PathVariable String employeeId,
            @RequestParam(required = false) String date
    ) {

        employeeId = employeeId.trim();

        LocalDate targetDate =
                (date == null || date.isEmpty())
                        ? LocalDate.now()
                        : LocalDate.parse(date);

        return attendanceService.calculateDailyAttendance(employeeId, targetDate);
    }
}