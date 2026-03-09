package com.example.attendance.dto;

public class DailyAttendanceDto {
    private String attendanceDate;
    private String status;
    private String finalInOfficeDuration;
    private String invalidReason;
    private String eventTime; // <-- NEW FIELD

    public DailyAttendanceDto() {}

    public String getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(String attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFinalInOfficeDuration() { return finalInOfficeDuration; }
    public void setFinalInOfficeDuration(String finalInOfficeDuration) { this.finalInOfficeDuration = finalInOfficeDuration; }

    public String getInvalidReason() { return invalidReason; }
    public void setInvalidReason(String invalidReason) { this.invalidReason = invalidReason; }

    public String getEventTime() { return eventTime; }         // <-- NEW
    public void setEventTime(String eventTime) { this.eventTime = eventTime; } // <-- NEW
}