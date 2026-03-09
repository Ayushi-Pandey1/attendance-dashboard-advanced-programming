package com.example.attendance.service;

import com.example.attendance.entity.AttendanceEvent;
import com.example.attendance.entity.DailyAttendanceEntity;
import com.example.attendance.model.AttendanceStatus;
import com.example.attendance.repository.AttendanceEventRepository;
import com.example.attendance.repository.DailyAttendanceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceProcessor {

 private final AttendanceEventRepository eventRepo;
 private final DailyAttendanceRepository dailyRepo;
 private final AttendanceService service;

 public AttendanceProcessor(AttendanceEventRepository eventRepo,
                            DailyAttendanceRepository dailyRepo,
                            AttendanceService service) {
  this.eventRepo = eventRepo;
  this.dailyRepo = dailyRepo;
  this.service = service;
 }

 /**
  * Polls new tap events
  */
 @Scheduled(fixedRate = 10000)
 public void pollAndProcess() {

  List<AttendanceEvent> events = eventRepo.findByProcessedFalse();

  for (AttendanceEvent e : events) {

   String emp = e.getEmployeeId();
   LocalDate date = e.getEventTime().toLocalDate();

   AttendanceResult result = service.processDay(emp, date);

   DailyAttendanceEntity entity =
           dailyRepo.findByEmployeeIdAndAttendanceDate(emp, date)
                   .orElse(new DailyAttendanceEntity(emp, date));

   entity.setStatus(result.getStatus());
   entity.setEventTime(e.getEventTime()); // <-- NEW LINE

   if (result.getStatus() == AttendanceStatus.IN_OFFICE) {
    entity.setFinalInOfficeDuration("ONGOING");
   } else {
    entity.setFinalInOfficeDuration(result.getFinalDuration());
   }

   entity.setInvalidReason(result.getInvalidReason());

   dailyRepo.save(entity);

   e.setProcessed(true);
   eventRepo.save(e);
  }
 }

 /**
  * Midnight job to mark forgotten tap-outs
  */
 @Scheduled(cron = "0 5 0 * * *")
 public void markIncompleteSessions() {

  LocalDate yesterday = LocalDate.now().minusDays(1);

  List<DailyAttendanceEntity> openSessions =
          dailyRepo.findByAttendanceDateAndStatus(
                  yesterday,
                  AttendanceStatus.IN_OFFICE
          );

  for (DailyAttendanceEntity entity : openSessions) {

   entity.setStatus(AttendanceStatus.INCOMPLETE);
   entity.setInvalidReason("Forgot to tap out");

   dailyRepo.save(entity);
  }
 }
}