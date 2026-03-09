package com.example.attendance.service;

import com.example.attendance.entity.AttendanceEvent;
import com.example.attendance.model.AttendanceStatus;
import com.example.attendance.repository.AttendanceEventRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Comparator;
import java.util.List;

@Service
public class AttendanceService {

 private final AttendanceEventRepository repository;

 public AttendanceService(AttendanceEventRepository repository) {
  this.repository = repository;
 }

 public AttendanceResult processDay(String employeeId, LocalDate date) {

  LocalDateTime start = date.atStartOfDay();
  LocalDateTime end = date.atTime(23, 59, 59);

  List<AttendanceEvent> events =
          repository.findByEmployeeIdAndEventTimeBetween(employeeId, start, end);

  events.sort(Comparator.comparing(AttendanceEvent::getEventTime));

  long totalMinutes = 0;
  LocalDateTime lastIn = null;

  for (AttendanceEvent e : events) {

   if ("IN".equalsIgnoreCase(e.getEventType())) {

    if (lastIn != null)
     return AttendanceResult.invalid("Double IN detected");

    lastIn = e.getEventTime();
   }

   else if ("OUT".equalsIgnoreCase(e.getEventType())) {

    if (lastIn == null)
     return AttendanceResult.invalid("OUT before IN detected");

    totalMinutes += Duration.between(lastIn, e.getEventTime()).toMinutes();
    lastIn = null;
   }
  }

  // ===== STILL INSIDE OFFICE =====
  if (lastIn != null) {

   // If processing past date → INCOMPLETE
   if (LocalDate.now().isAfter(date)) {
    return AttendanceResult.incomplete(totalMinutes);
   }

   // Employee still in office
   return AttendanceResult.inOffice(0);
  }

  // ===== COMPLETED DAY =====
  if (totalMinutes >= 240)
   return AttendanceResult.completed(totalMinutes, AttendanceStatus.PRESENT);

  if (totalMinutes > 0)
   return AttendanceResult.completed(totalMinutes, AttendanceStatus.PARTIAL);

  return AttendanceResult.completed(0, AttendanceStatus.PARTIAL);
 }
}