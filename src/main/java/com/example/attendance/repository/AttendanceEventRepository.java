package com.example.attendance.repository;

import com.example.attendance.model.AttendanceEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceEventRepository
        extends JpaRepository<AttendanceEvent, Long> {

    @Query("""
        SELECT e
        FROM AttendanceEvent e
        WHERE e.employeeId = :employeeId
          AND e.eventTime BETWEEN :start AND :end
        ORDER BY e.eventTime
    """)
    List<AttendanceEvent> findByEmployeeAndDateRange(
            @Param("employeeId") String employeeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}