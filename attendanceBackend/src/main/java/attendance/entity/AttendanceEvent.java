package attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "attendance_event")
public class AttendanceEvent {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false)
 private String employeeId;

 /** Timestamp the server received the tap. */
 @Column(nullable = false)
 private LocalDateTime eventTime;

 /** "IN" when entering the office, "OUT" when leaving. Always uppercase. */
 @Column(nullable = false)
 private String eventType;

 /** False until AttendanceProcessor has consumed and aggregated this event. */
 @Column(nullable = false)
 private boolean processed = false;

 public Long          getId()         { return id; }
 public String        getEmployeeId() { return employeeId; }
 public LocalDateTime getEventTime()  { return eventTime; }
 public String        getEventType()  { return eventType; }
 public boolean       isProcessed()   { return processed; }

 public void setEmployeeId(String employeeId)       { this.employeeId = employeeId; }
 public void setEventTime(LocalDateTime eventTime)  { this.eventTime  = eventTime; }
 public void setEventType(String eventType)         { this.eventType  = eventType; }
 public void setProcessed(boolean processed)        { this.processed  = processed; }
}
