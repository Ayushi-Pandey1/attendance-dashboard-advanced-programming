package attendance.controller;

import attendance.dto.DailyAttendanceDto;
import attendance.dto.TapRequestDto;
import attendance.dto.TodayStatusDto;
import attendance.entity.AttendanceEvent;
import attendance.entity.DailyAttendanceEntity;
import attendance.model.AttendanceStatus;
import attendance.repository.AttendanceEventRepository;
import attendance.repository.DailyAttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AttendanceController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

    private final DailyAttendanceRepository dailyRepo;
    private final AttendanceEventRepository eventRepo;

    public AttendanceController(DailyAttendanceRepository dailyRepo,
                                AttendanceEventRepository eventRepo) {
        this.dailyRepo = dailyRepo;
        this.eventRepo = eventRepo;
    }

    /**
     * Records a tap-in or tap-out event for an employee.
     * Saves the event as unprocessed; the background job handles aggregation.
     */
    @PostMapping("/tap")
    public ResponseEntity<String> tap(@RequestBody TapRequestDto request) {
        log.info("Tap event received — employeeId={}, eventType={}",
                request.getEmployeeId(), request.getEventType());

        AttendanceEvent event = new AttendanceEvent();
        event.setEmployeeId(request.getEmployeeId());
        event.setEventType(request.getEventType().toUpperCase());
        event.setEventTime(LocalDateTime.now().withNano(0));
        event.setProcessed(false);

        eventRepo.save(event);
        log.debug("AttendanceEvent persisted — id={}, employeeId={}, type={}, time={}",
                event.getId(), event.getEmployeeId(), event.getEventType(), event.getEventTime());

        return ResponseEntity.ok("Event recorded");
    }

    /**
     * Returns today's attendance status for the given employee.
     * If the employee is IN_OFFICE, also returns the last tap-in time for live timer use.
     */
    @GetMapping("/today/{employeeId}")
    public TodayStatusDto getToday(@PathVariable String employeeId) {
        log.debug("Today-status request — employeeId={}", employeeId);

        LocalDate today = LocalDate.now();
        TodayStatusDto dto = new TodayStatusDto();
        dto.setEmployeeId(employeeId);

        Optional<DailyAttendanceEntity> opt =
                dailyRepo.findByEmployeeIdAndAttendanceDate(employeeId, today);

        if (opt.isEmpty()) {
            log.debug("No attendance record found for employeeId={} on {}", employeeId, today);
            dto.setStatus("NO_DATA");
            return dto;
        }

        DailyAttendanceEntity entity = opt.get();
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : "NO_DATA");
        dto.setDuration(entity.getFinalInOfficeDuration());

        // If currently in office, replay today's events to find the last unmatched tap-in
        if (entity.getStatus() == AttendanceStatus.IN_OFFICE) {
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay   = today.atTime(23, 59, 59);

            List<AttendanceEvent> events = eventRepo
                    .findByEmployeeIdAndEventTimeBetweenOrderByEventTimeAsc(
                            employeeId, startOfDay, endOfDay);

            LocalDateTime lastIn = null;
            for (AttendanceEvent e : events) {
                if ("IN".equalsIgnoreCase(e.getEventType())) {
                    lastIn = e.getEventTime();
                } else if ("OUT".equalsIgnoreCase(e.getEventType())) {
                    lastIn = null; // matched pair — reset
                }
            }

            if (lastIn != null) {
                dto.setLastInTime(lastIn.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                log.debug("Employee {} is IN_OFFICE since {}", employeeId, lastIn);
            }
        }

        log.debug("Returning today-status for employeeId={}: status={}", employeeId, dto.getStatus());
        return dto;
    }

    /**
     * Alias for getToday — kept for backwards compatibility with older clients.
     */
    @GetMapping("/status/{employeeId}")
    public TodayStatusDto getStatus(@PathVariable String employeeId) {
        log.debug("Status alias hit — delegating to getToday for employeeId={}", employeeId);
        return getToday(employeeId);
    }

    /**
     * Returns the full attendance history for an employee, most-recent-first.
     */
    @GetMapping("/history/{employeeId}")
    public List<DailyAttendanceDto> getHistory(@PathVariable String employeeId) {
        log.info("History request — employeeId={}", employeeId);

        List<DailyAttendanceEntity> records =
                dailyRepo.findByEmployeeIdOrderByAttendanceDateDesc(employeeId);

        log.debug("Found {} historical records for employeeId={}", records.size(), employeeId);
        return records.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Returns attendance records for a specific month.
     * Defaults to the current year/month if year or month params are omitted.
     */
    @GetMapping("/month/{employeeId}")
    public List<DailyAttendanceDto> getMonth(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {

        LocalDate now = LocalDate.now();
        int y = year  > 0 ? year  : now.getYear();
        int m = month > 0 ? month : now.getMonthValue();

        LocalDate from = LocalDate.of(y, m, 1);
        LocalDate to   = from.withDayOfMonth(from.lengthOfMonth());

        log.info("Monthly attendance request — employeeId={}, period={} to {}", employeeId, from, to);

        List<DailyAttendanceEntity> records =
                dailyRepo.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        employeeId, from, to);

        log.debug("Found {} records for employeeId={} in {}/{}", records.size(), employeeId, m, y);
        return records.stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Maps a DailyAttendanceEntity to its API DTO. Dates serialised as ISO-8601 strings. */
    private DailyAttendanceDto toDto(DailyAttendanceEntity e) {
        DailyAttendanceDto dto = new DailyAttendanceDto();
        dto.setAttendanceDate(e.getAttendanceDate().toString());
        dto.setStatus(e.getStatus() != null ? e.getStatus().name() : null);
        dto.setFinalInOfficeDuration(e.getFinalInOfficeDuration());
        dto.setInvalidReason(e.getInvalidReason());
        return dto;
    }
}
