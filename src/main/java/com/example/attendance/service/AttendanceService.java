package com.example.attendance.service;

import com.example.attendance.model.*;
import com.example.attendance.repository.AttendanceEventRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceEventRepository repository;

    public AttendanceService(AttendanceEventRepository repository) {
        this.repository = repository;
    }

    public DailyAttendanceResult calculateDailyAttendance(
            String employeeId,
            LocalDate date
    ) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<AttendanceEvent> events =
                repository.findByEmployeeAndDateRange(
                        employeeId,
                        startOfDay,
                        endOfDay
                );

        if (events.isEmpty()) {
            return new DailyAttendanceResult(
                    employeeId,
                    date,
                    new ArrayList<>(),
                    Duration.ZERO,
                    AttendanceStatus.ABSENT
            );
        }

        List<Session> sessions = new ArrayList<>();
        Duration totalWorked = Duration.ZERO;

        LocalDateTime lastIn = null;
        boolean invalid = false;

        for (AttendanceEvent event : events) {

            if (event.getEventType() == EventType.IN) {

                if (lastIn != null) {
                    // Double IN without OUT
                    invalid = true;
                    break;
                }

                lastIn = event.getEventTime();
            }

            else if (event.getEventType() == EventType.OUT) {

                if (lastIn == null) {
                    // OUT before IN
                    invalid = true;
                    break;
                }

                LocalDateTime outTime = event.getEventTime();

                if (outTime.isBefore(lastIn)) {
                    invalid = true;
                    break;
                }

                sessions.add(new Session(lastIn, outTime));
                totalWorked = totalWorked.plus(Duration.between(lastIn, outTime));
                lastIn = null;
            }
        }

        if (invalid) {
            return new DailyAttendanceResult(
                    employeeId,
                    date,
                    new ArrayList<>(),
                    Duration.ZERO,
                    AttendanceStatus.INVALID
            );
        }

        LocalDateTime now = LocalDateTime.now();

        // Case: Still inside office
        if (lastIn != null) {

            if (date.equals(LocalDate.now())) {

                Duration liveDuration = Duration.between(lastIn, now);
                totalWorked = totalWorked.plus(liveDuration);

                sessions.add(new Session(lastIn, now));

                return new DailyAttendanceResult(
                        employeeId,
                        date,
                        sessions,
                        totalWorked,
                        AttendanceStatus.IN_OFFICE_NOW
                );
            }
            else {
                // Day is over, but no OUT event
                return new DailyAttendanceResult(
                        employeeId,
                        date,
                        new ArrayList<>(),
                        Duration.ZERO,
                        AttendanceStatus.INCOMPLETE
                );
            }
        }

        long hoursWorked = totalWorked.toHours();

        AttendanceStatus finalStatus;

        if (hoursWorked < 4) {
            finalStatus = AttendanceStatus.PARTIAL;
        } else {
            finalStatus = AttendanceStatus.PRESENT;
        }

        return new DailyAttendanceResult(
                employeeId,
                date,
                sessions,
                totalWorked,
                finalStatus
        );
    }
}