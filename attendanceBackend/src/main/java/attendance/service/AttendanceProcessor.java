package attendance.service;

import attendance.entity.AttendanceEvent;
import attendance.entity.DailyAttendanceEntity;
import attendance.model.AttendanceStatus;
import attendance.repository.AttendanceEventRepository;
import attendance.repository.DailyAttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Background service with two scheduled jobs:
 * 1. pollAndProcess — runs every 10s, processes unprocessed tap events.
 * 2. markIncompleteSessions — runs at 00:05 daily, closes forgotten tap-outs.
 */
@Service
public class AttendanceProcessor {

 private static final Logger log = LoggerFactory.getLogger(AttendanceProcessor.class);

 private final AttendanceEventRepository eventRepo;
 private final DailyAttendanceRepository dailyRepo;
 private final AttendanceService service;

 public AttendanceProcessor(AttendanceEventRepository eventRepo,
                            DailyAttendanceRepository dailyRepo,
                            AttendanceService service) {
  this.eventRepo = eventRepo;
  this.dailyRepo = dailyRepo;
  this.service   = service;
 }

 /**
  * Polls for unprocessed tap events and updates the daily attendance summary.
  * Each event is marked as processed after a successful upsert.
  * Errors are caught per-event so one bad record doesn't block the whole batch.
  */
 @Scheduled(fixedRate = 10_000)
 public void pollAndProcess() {
  List<AttendanceEvent> events = eventRepo.findByProcessedFalse();

  if (events.isEmpty()) {
   log.trace("pollAndProcess — no unprocessed events found");
   return;
  }

  log.info("pollAndProcess — processing {} unprocessed event(s)", events.size());

  for (AttendanceEvent e : events) {
   String    emp  = e.getEmployeeId();
   LocalDate date = e.getEventTime().toLocalDate();

   log.debug("Processing event id={} — employeeId={}, type={}, time={}",
           e.getId(), emp, e.getEventType(), e.getEventTime());

   try {
    AttendanceResult result = service.processDay(emp, date);

    // Upsert: update existing record or create a new one for this employee/date
    DailyAttendanceEntity entity =
            dailyRepo.findByEmployeeIdAndAttendanceDate(emp, date)
                    .orElse(new DailyAttendanceEntity(emp, date));

    entity.setStatus(result.getStatus());
    entity.setEventTime(e.getEventTime());
    // Show ONGOING while the session is still live; set final duration once complete
    entity.setFinalInOfficeDuration(
            result.getStatus() == AttendanceStatus.IN_OFFICE
                    ? "ONGOING"
                    : result.getFinalDuration()
    );
    entity.setInvalidReason(result.getInvalidReason());
    dailyRepo.save(entity);

    // Mark processed to prevent this event being handled again
    e.setProcessed(true);
    eventRepo.save(e);

    log.info("Event processed — employeeId={}, date={}, newStatus={}",
            emp, date, result.getStatus());

   } catch (Exception ex) {
    log.error("Failed to process event id={} for employeeId={}: {}",
            e.getId(), emp, ex.getMessage(), ex);
   }
  }
 }

 /**
  * Finds any sessions still marked IN_OFFICE from yesterday and sets them to INCOMPLETE.
  * Runs at 00:05 (not midnight) to avoid a race with events tapped just before midnight.
  */
 @Scheduled(cron = "0 5 0 * * *")
 public void markIncompleteSessions() {
  LocalDate yesterday = LocalDate.now().minusDays(1);

  log.info("markIncompleteSessions — scanning for open sessions on {}", yesterday);

  List<DailyAttendanceEntity> openSessions =
          dailyRepo.findByAttendanceDateAndStatus(yesterday, AttendanceStatus.IN_OFFICE);

  if (openSessions.isEmpty()) {
   log.info("markIncompleteSessions — no open sessions found for {}", yesterday);
   return;
  }

  log.warn("markIncompleteSessions — {} open session(s) on {}; marking INCOMPLETE",
          openSessions.size(), yesterday);

  for (DailyAttendanceEntity entity : openSessions) {
   entity.setStatus(AttendanceStatus.INCOMPLETE);
   entity.setInvalidReason("Forgot to tap out");
   dailyRepo.save(entity);
   log.info("Session marked INCOMPLETE — employeeId={}, date={}",
           entity.getEmployeeId(), yesterday);
  }
 }
}
