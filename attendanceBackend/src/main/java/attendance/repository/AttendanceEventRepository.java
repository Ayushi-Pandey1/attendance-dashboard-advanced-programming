package attendance.repository;

import attendance.entity.AttendanceEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface AttendanceEventRepository extends JpaRepository<AttendanceEvent, Long> {

    /** Returns all events not yet consumed by the background processor. */
    List<AttendanceEvent> findByProcessedFalse();

    /** Returns all events for an employee within a datetime window, unsorted. */
    List<AttendanceEvent> findByEmployeeIdAndEventTimeBetween(
            String employeeId, LocalDateTime start, LocalDateTime end);

    /** Returns all events for an employee within a datetime window, sorted oldest-first. */
    List<AttendanceEvent> findByEmployeeIdAndEventTimeBetweenOrderByEventTimeAsc(
            String employeeId, LocalDateTime start, LocalDateTime end);
}
