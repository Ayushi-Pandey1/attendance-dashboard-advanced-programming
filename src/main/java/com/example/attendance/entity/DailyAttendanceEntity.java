package com.example.attendance.entity;

import com.example.attendance.model.AttendanceStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employeeId","attendanceDate"}))
public class DailyAttendanceEntity {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String employeeId;

 @Column(nullable = false)
 private LocalDate attendanceDate;

 @Column(name = "final_in_office_duration")
 private String finalInOfficeDuration;


 @Enumerated(EnumType.STRING)
 private AttendanceStatus status;

 private String invalidReason;

 public DailyAttendanceEntity() {}

 public DailyAttendanceEntity(String employeeId, LocalDate attendanceDate) {
  this.employeeId = employeeId;
  this.attendanceDate = attendanceDate;
 }

 // ===== GETTERS AND SETTERS =====

 public Long getId() { return id; }

 public String getEmployeeId() { return employeeId; }

 public void setEmployeeId(String employeeId) {
  this.employeeId = employeeId;
 }

 public LocalDate getAttendanceDate() { return attendanceDate; }

 public void setAttendanceDate(LocalDate attendanceDate) {
  this.attendanceDate = attendanceDate;
 }

 public String getFinalInOfficeDuration() { return finalInOfficeDuration; }

 public void setFinalInOfficeDuration(String finalInOfficeDuration) {
  this.finalInOfficeDuration = finalInOfficeDuration;
 }

 public AttendanceStatus getStatus() { return status; }

 public void setStatus(AttendanceStatus status) {
  this.status = status;
 }

 public String getInvalidReason() { return invalidReason; }

 public void setInvalidReason(String invalidReason) {
  this.invalidReason = invalidReason;
 }
}