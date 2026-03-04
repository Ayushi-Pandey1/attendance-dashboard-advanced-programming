package com.example.attendance.service;

import com.example.attendance.entity.AttendanceEvent;
import com.example.attendance.entity.DailyAttendanceEntity;
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
   entity.setFinalInOfficeDuration(result.getFinalDuration());
   entity.setCurrentInOfficeDuration(result.getCurrentDuration());
   entity.setInvalidReason(result.getInvalidReason());

   dailyRepo.save(entity);

   e.setProcessed(true);
   eventRepo.save(e);
  }
 }
}