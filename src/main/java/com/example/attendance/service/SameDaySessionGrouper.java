package com.example.attendance.service;

import com.example.attendance.model.AttendanceEvent;
import com.example.attendance.model.AttendanceSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SameDaySessionGrouper {

    public List<AttendanceSession> groupSessions(List<AttendanceEvent> events) {

        List<AttendanceSession> sessions = new ArrayList<>();

        AttendanceEvent lastIn = null;

        for (AttendanceEvent event : events) {

            if ("IN".equals(event.getEventType())) {

                // store IN event
                lastIn = event;

            } else if ("OUT".equals(event.getEventType()) && lastIn != null) {

                AttendanceSession session = new AttendanceSession(
                        lastIn.getEventTime(),
                        event.getEventTime()
                );

                sessions.add(session);

                // reset pairing
                lastIn = null;
            }
        }

        return sessions;
    }
}