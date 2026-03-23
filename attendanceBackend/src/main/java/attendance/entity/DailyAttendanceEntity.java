package attendance.entity;

import attendance.model.AttendanceStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "daily_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employeeId", "attendanceDate"})
)
public class DailyAttendanceEntity {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String employeeId;

 @Column(nullable = false)
 private LocalDate attendanceDate;

 /** Total time in office, e.g. "3 hours 45 minutes". Set to "ONGOING" while session is live. */
 @Column(name = "final_in_office_duration")
 private String finalInOfficeDuration;

 /** Timestamp of the last tap event that triggered an update to this row. */
 @Column(name = "event_time")
 private LocalDateTime eventTime;

 /** Computed status for the day: PRESENT, PARTIAL, IN_OFFICE, INCOMPLETE, or INVALID. */
 @Enumerated(EnumType.STRING)
 private AttendanceStatus status;

 /** Human-readable reason, set only when status is INCOMPLETE or INVALID. */
 private String invalidReason;

 public DailyAttendanceEntity() {}

 public DailyAttendanceEntity(String employeeId, LocalDate attendanceDate) {
  this.employeeId     = employeeId;
  this.attendanceDate = attendanceDate;
 }

 public Long             getId()                       { return id; }
 public String           getEmployeeId()               { return employeeId; }
 public void             setEmployeeId(String v)       { this.employeeId = v; }
 public LocalDate        getAttendanceDate()           { return attendanceDate; }
 public void             setAttendanceDate(LocalDate v){ this.attendanceDate = v; }
 public String           getFinalInOfficeDuration()    { return finalInOfficeDuration; }
 public void             setFinalInOfficeDuration(String v) { this.finalInOfficeDuration = v; }
 public LocalDateTime    getEventTime()                { return eventTime; }
 public void             setEventTime(LocalDateTime v) { this.eventTime = v; }
 public AttendanceStatus getStatus()                   { return status; }
 public void             setStatus(AttendanceStatus v) { this.status = v; }
 public String           getInvalidReason()            { return invalidReason; }
 public void             setInvalidReason(String v)    { this.invalidReason = v; }
}
