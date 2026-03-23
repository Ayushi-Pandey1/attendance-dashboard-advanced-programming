package attendance.service;

import attendance.entity.AttendanceEvent;
import attendance.model.AttendanceStatus;
import attendance.repository.AttendanceEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Comparator;
import java.util.List;

/**
 * Core business logic for computing daily attendance status.
 * Replays tap events in chronological order to determine total in-office time
 * and classify the day as PRESENT, PARTIAL, IN_OFFICE, INCOMPLETE, or INVALID.
 */
@Service
public class AttendanceService {

 private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

 /** Minimum in-office minutes required to be classified as PRESENT. */
 private static final int FULL_DAY_THRESHOLD_MINUTES = 240;

 private final AttendanceEventRepository repository;

 public AttendanceService(AttendanceEventRepository repository) {
  this.repository = repository;
 }

 /**
  * Computes the attendance result for a single employee on a given date.
  * Fetches all events for the day, sorts them, and pairs each IN with the next OUT.
  * Returns INVALID if the sequence is inconsistent (e.g. double-IN or OUT before IN).
  */
 public AttendanceResult processDay(String employeeId, LocalDate date) {
  log.debug("processDay called — employeeId={}, date={}", employeeId, date);

  LocalDateTime start = date.atStartOfDay();
  LocalDateTime end   = date.atTime(23, 59, 59);

  List<AttendanceEvent> events =
          repository.findByEmployeeIdAndEventTimeBetween(employeeId, start, end);

  events.sort(Comparator.comparing(AttendanceEvent::getEventTime));

  log.debug("Found {} event(s) for employeeId={} on {}", events.size(), employeeId, date);

  long totalMinutes = 0;
  LocalDateTime lastIn = null;

  for (AttendanceEvent e : events) {

   if ("IN".equalsIgnoreCase(e.getEventType())) {
    if (lastIn != null) {
     // Two consecutive INs with no OUT in between — data is corrupt
     log.warn("Double IN detected — employeeId={}, date={}, firstIn={}, secondIn={}",
             employeeId, date, lastIn, e.getEventTime());
     return AttendanceResult.invalid("Double IN detected");
    }
    lastIn = e.getEventTime();
    log.trace("IN recorded — employeeId={}, time={}", employeeId, lastIn);

   } else if ("OUT".equalsIgnoreCase(e.getEventType())) {
    if (lastIn == null) {
     // OUT with no preceding IN — data is corrupt
     log.warn("OUT before IN detected — employeeId={}, date={}, outTime={}",
             employeeId, date, e.getEventTime());
     return AttendanceResult.invalid("OUT before IN detected");
    }
    long sessionMinutes = Duration.between(lastIn, e.getEventTime()).toMinutes();
    totalMinutes += sessionMinutes;
    log.debug("OUT paired — employeeId={}, sessionMinutes={}, runningTotal={}",
            employeeId, sessionMinutes, totalMinutes);
    lastIn = null; // reset for the next IN/OUT pair
   }
  }

  // Unmatched tap-in: employee is still in the office, or day ended without tap-out
  if (lastIn != null) {
   if (LocalDate.now().isAfter(date)) {
    log.info("Incomplete session — employeeId={}, date={}, lastIn={}", employeeId, date, lastIn);
    return AttendanceResult.incomplete(totalMinutes);
   }
   log.debug("Employee currently in office — employeeId={}, lastIn={}", employeeId, lastIn);
   return AttendanceResult.inOffice(totalMinutes);
  }

  // All pairs matched — classify by total time
  if (totalMinutes >= FULL_DAY_THRESHOLD_MINUTES) {
   log.info("PRESENT — employeeId={}, date={}, totalMinutes={}", employeeId, date, totalMinutes);
   return AttendanceResult.completed(totalMinutes, AttendanceStatus.PRESENT);
  }
  if (totalMinutes > 0) {
   log.info("PARTIAL — employeeId={}, date={}, totalMinutes={}", employeeId, date, totalMinutes);
   return AttendanceResult.completed(totalMinutes, AttendanceStatus.PARTIAL);
  }

  log.debug("No time recorded — employeeId={}, date={}", employeeId, date);
  return AttendanceResult.completed(0, AttendanceStatus.PARTIAL);
 }
}
