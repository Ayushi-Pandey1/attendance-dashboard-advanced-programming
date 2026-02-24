package com.example.attendance.service;

import com.example.attendance.model.AttendanceEvent;
import com.example.attendance.model.AttendanceStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AttendanceStatusEngine {

    private static final long FOUR_HOURS_SECONDS = 4 * 60 * 60;

    public AttendanceStatus determineStatus(
            List<AttendanceEvent> events,
            long workedSeconds) {

        if (events == null || events.isEmpty()) {
            return AttendanceStatus.ABSENT;
        }

        // Sort events by time
        events.sort(Comparator.comparing(AttendanceEvent::getEventTime));

        AttendanceEvent lastEvent = events.get(events.size() - 1);

        // If last event is IN → employee currently inside
        if ("IN".equals(lastEvent.getEventType())) {
            return AttendanceStatus.IN_OFFICE_NOW;
        }

        // Check total worked time
        if (workedSeconds >= FOUR_HOURS_SECONDS) {
            return AttendanceStatus.PRESENT;
        }

        if (workedSeconds > 0) {
            return AttendanceStatus.PARTIAL;
        }

        return AttendanceStatus.ABSENT;
    }
}