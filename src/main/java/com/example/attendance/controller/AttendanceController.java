package com.example.attendance.controller;

import com.example.attendance.dto.DailyAttendanceDto;
import com.example.attendance.dto.TodayStatusDto;
import com.example.attendance.entity.AttendanceEvent;
import com.example.attendance.entity.DailyAttendanceEntity;
import com.example.attendance.model.AttendanceStatus;
import com.example.attendance.repository.AttendanceEventRepository;
import com.example.attendance.repository.DailyAttendanceRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AttendanceController {

    private final DailyAttendanceRepository dailyRepo;
    private final AttendanceEventRepository eventRepo;

    public AttendanceController(DailyAttendanceRepository dailyRepo,
                                AttendanceEventRepository eventRepo) {
        this.dailyRepo = dailyRepo;
        this.eventRepo = eventRepo;
    }

    /**
     * GET /api/attendance/today/{employeeId}
     * Returns today's status including lastInTime for live timer calculation.
     */
    @GetMapping("/today/{employeeId}")
    public TodayStatusDto getToday(@PathVariable String employeeId) {
        LocalDate today = LocalDate.now();
        TodayStatusDto dto = new TodayStatusDto();
        dto.setEmployeeId(employeeId);

        Optional<DailyAttendanceEntity> opt =
                dailyRepo.findByEmployeeIdAndAttendanceDate(employeeId, today);

        if (opt.isEmpty()) {
            dto.setStatus("NO_DATA");
            return dto;
        }

        DailyAttendanceEntity entity = opt.get();
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : "NO_DATA");
        dto.setDuration(entity.getFinalInOfficeDuration());

        // If currently IN_OFFICE, find the last unmatched IN event to return lastInTime
        if (entity.getStatus() == AttendanceStatus.IN_OFFICE) {
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(23, 59, 59);

            List<AttendanceEvent> events = eventRepo
                    .findByEmployeeIdAndEventTimeBetweenOrderByEventTimeAsc(
                            employeeId, startOfDay, endOfDay);

            LocalDateTime lastIn = null;
            for (AttendanceEvent e : events) {
                if ("IN".equalsIgnoreCase(e.getEventType())) {
                    lastIn = e.getEventTime();
                } else if ("OUT".equalsIgnoreCase(e.getEventType())) {
                    lastIn = null;
                }
            }

            if (lastIn != null) {
                dto.setLastInTime(lastIn.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        }

        return dto;
    }

    /**
     * GET /api/attendance/status/{employeeId}
     * Alias for today — used by status badge component.
     */
    @GetMapping("/status/{employeeId}")
    public TodayStatusDto getStatus(@PathVariable String employeeId) {
        return getToday(employeeId);
    }

    /**
     * GET /api/attendance/history/{employeeId}
     * Returns all attendance records, newest first.
     */
    @GetMapping("/history/{employeeId}")
    public List<DailyAttendanceDto> getHistory(@PathVariable String employeeId) {
        List<DailyAttendanceEntity> records =
                dailyRepo.findByEmployeeIdOrderByAttendanceDateDesc(employeeId);
        return records.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * GET /api/attendance/month/{employeeId}?year=2024&month=6
     * Returns attendance data for a given month.
     */
    @GetMapping("/month/{employeeId}")
    public List<DailyAttendanceDto> getMonth(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {

        LocalDate now = LocalDate.now();
        int y = year > 0 ? year : now.getYear();
        int m = month > 0 ? month : now.getMonthValue();

        LocalDate from = LocalDate.of(y, m, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        List<DailyAttendanceEntity> records =
                dailyRepo.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        employeeId, from, to);

        return records.stream().map(this::toDto).collect(Collectors.toList());
    }

    private DailyAttendanceDto toDto(DailyAttendanceEntity e) {
        DailyAttendanceDto dto = new DailyAttendanceDto();
        dto.setAttendanceDate(e.getAttendanceDate().toString());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setFinalInOfficeDuration(e.getFinalInOfficeDuration());
        dto.setInvalidReason(e.getInvalidReason());
        return dto;
    }
}
