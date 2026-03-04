
package com.example.attendance.repository;

import com.example.attendance.entity.DailyAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyAttendanceRepository
 extends JpaRepository<DailyAttendanceEntity,Long>{

 Optional<DailyAttendanceEntity> findByEmployeeIdAndAttendanceDate(
  String employeeId, LocalDate attendanceDate);
}
