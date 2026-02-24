package com.example.attendance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class DailyAttendanceResult {

    private String employeeId;
    private LocalDate date;
    private List<Session> sessions;

    @JsonIgnore
    private Duration totalWorked;

    private AttendanceStatus status;

    public DailyAttendanceResult(
            String employeeId,
            LocalDate date,
            List<Session> sessions,
            Duration totalWorked,
            AttendanceStatus status) {

        this.employeeId = employeeId;
        this.date = date;
        this.sessions = sessions;
        this.totalWorked = totalWorked;
        this.status = status;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    @JsonProperty("totalWorked")
    public String getFormattedTotalWorked() {
        if (totalWorked == null || totalWorked.isZero()) {
            return "0 hours 0 minutes";
        }

        long hours = totalWorked.toHours();
        long minutes = totalWorked.minusHours(hours).toMinutes();

        String hourLabel = hours == 1 ? "hour" : "hours";
        String minuteLabel = minutes == 1 ? "minute" : "minutes";

        return String.format("%d %s %d %s", hours, hourLabel, minutes, minuteLabel);
    }
}