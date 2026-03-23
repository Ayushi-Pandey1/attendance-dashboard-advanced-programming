package attendance.repository;

import attendance.entity.DailyAttendanceEntity;
import attendance.model.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface DailyAttendanceRepository extends JpaRepository<DailyAttendanceEntity, Long> {

    /** Finds the attendance summary for one employee on one date. Empty if none exists yet. */
    Optional<DailyAttendanceEntity> findByEmployeeIdAndAttendanceDate(
            String employeeId, LocalDate attendanceDate);

    /** Finds all summaries for a given date matching a specific status. Used by the midnight job. */
    List<DailyAttendanceEntity> findByAttendanceDateAndStatus(
            LocalDate attendanceDate, AttendanceStatus status);

    /** Returns the full attendance history for an employee, most-recent-first. */
    List<DailyAttendanceEntity> findByEmployeeIdOrderByAttendanceDateDesc(String employeeId);

    /** Returns attendance records for an employee within a date range, oldest-first. */
    List<DailyAttendanceEntity> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            String employeeId, LocalDate from, LocalDate to);
}
