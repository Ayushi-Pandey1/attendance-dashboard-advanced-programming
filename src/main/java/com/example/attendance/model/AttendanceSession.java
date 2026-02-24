package com.example.attendance.model;

import java.time.LocalDateTime;

public class AttendanceSession {

    private LocalDateTime inTime;
    private LocalDateTime outTime;

    public AttendanceSession(LocalDateTime inTime, LocalDateTime outTime) {
        this.inTime = inTime;
        this.outTime = outTime;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }
}