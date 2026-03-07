package com.example.attendance.dto;

public class TodayStatusDto {
    private String employeeId;
    private String status;
    private String lastInTime;   // ISO-8601 string, null if not IN_OFFICE
    private String duration;     // "ONGOING" or formatted string

    public TodayStatusDto() {}

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLastInTime() { return lastInTime; }
    public void setLastInTime(String lastInTime) { this.lastInTime = lastInTime; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
