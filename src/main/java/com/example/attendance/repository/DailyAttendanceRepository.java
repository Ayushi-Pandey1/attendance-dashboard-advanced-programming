package com.example.attendance.repository;

import com.example.attendance.entity.DailyAttendanceEntity;
import com.example.attendance.model.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyAttendanceRepository
        extends JpaRepository<DailyAttendanceEntity, Long> {

    Optional<DailyAttendanceEntity> findByEmployeeIdAndAttendanceDate(
            String employeeId, LocalDate attendanceDate);

    List<DailyAttendanceEntity> findByAttendanceDateAndStatus(
            LocalDate attendanceDate,
            AttendanceStatus status);

    List<DailyAttendanceEntity> findByEmployeeIdOrderByAttendanceDateDesc(
            String employeeId);

    List<DailyAttendanceEntity> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            String employeeId, LocalDate from, LocalDate to);
}
