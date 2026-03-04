
package com.example.attendance.repository;

import com.example.attendance.entity.AttendanceEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface AttendanceEventRepository
 extends JpaRepository<AttendanceEvent,Long>{

 List<AttendanceEvent> findByProcessedFalse();

 List<AttendanceEvent> findByEmployeeIdAndEventTimeBetween(
  String employeeId, LocalDateTime start, LocalDateTime end);
}
