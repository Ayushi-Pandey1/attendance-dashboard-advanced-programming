package com.example.attendance.dto;

public class TapRequestDto {
    private String employeeId;
    private String eventType;

    public String getEmployeeId() { return employeeId; }
    public String getEventType() { return eventType; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}
