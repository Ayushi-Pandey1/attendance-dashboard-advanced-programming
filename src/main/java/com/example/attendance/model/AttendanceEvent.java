package com.example.attendance.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_event")
public class AttendanceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;

    private LocalDateTime eventTime;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    public Long getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public EventType getEventType() {
        return eventType;
    }
}