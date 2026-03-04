package com.example.attendance.service;

import com.example.attendance.model.AttendanceStatus;

public class AttendanceResult {

    private AttendanceStatus status;
    private String finalDuration;
    private String currentDuration;
    private String invalidReason;

    private AttendanceResult() {}

    public static AttendanceResult completed(long minutes, AttendanceStatus status) {
        AttendanceResult r = new AttendanceResult();
        r.status = status;
        r.finalDuration = format(minutes);
        return r;
    }

    public static AttendanceResult inOffice(long minutes) {
        AttendanceResult r = new AttendanceResult();
        r.status = AttendanceStatus.IN_OFFICE;
        r.currentDuration = format(minutes);
        return r;
    }

    public static AttendanceResult incomplete(long minutes) {
        AttendanceResult r = new AttendanceResult();
        r.status = AttendanceStatus.INCOMPLETE;
        r.finalDuration = format(minutes);
        r.invalidReason = "Did not tap OUT";
        return r;
    }

    public static AttendanceResult invalid(String reason) {
        AttendanceResult r = new AttendanceResult();
        r.status = AttendanceStatus.INVALID;
        r.invalidReason = reason;
        return r;
    }

    private static String format(long minutes) {
        long hours = minutes / 60;
        long remaining = minutes % 60;
        return hours + " hours " + remaining + " minutes";
    }

    public AttendanceStatus getStatus() { return status; }
    public String getFinalDuration() { return finalDuration; }
    public String getCurrentDuration() { return currentDuration; }
    public String getInvalidReason() { return invalidReason; }
}