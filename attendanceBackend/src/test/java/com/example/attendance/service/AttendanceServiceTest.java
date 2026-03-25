package attendance.service;

import attendance.entity.AttendanceEvent;
import attendance.model.AttendanceStatus;
import attendance.repository.AttendanceEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceEventRepository repository;

    @InjectMocks
    private AttendanceService service;

    @Test
    void returnsPresentForFourHoursOrMore() {
        LocalDate date = LocalDate.of(2026, 3, 24);
        when(repository.findByEmployeeIdAndEventTimeBetween(
                "EMP001", date.atStartOfDay(), date.atTime(23, 59, 59)
        )).thenReturn(new ArrayList<>(List.of(
                event("EMP001", "IN", date.atTime(9, 0)),
                event("EMP001", "OUT", date.atTime(13, 30))
        )));

        AttendanceResult result = service.processDay("EMP001", date);

        assertEquals(AttendanceStatus.PRESENT, result.getStatus());
        assertEquals("4 hours 30 minutes", result.getFinalDuration());
        assertNull(result.getInvalidReason());
    }

    @Test
    void returnsPartialWhenWorkedLessThanFourHours() {
        LocalDate date = LocalDate.of(2026, 3, 24);
        when(repository.findByEmployeeIdAndEventTimeBetween(
                "EMP001", date.atStartOfDay(), date.atTime(23, 59, 59)
        )).thenReturn(new ArrayList<>(List.of(
                event("EMP001", "IN", date.atTime(9, 0)),
                event("EMP001", "OUT", date.atTime(11, 0))
        )));

        AttendanceResult result = service.processDay("EMP001", date);

        assertEquals(AttendanceStatus.PARTIAL, result.getStatus());
        assertEquals("2 hours 0 minutes", result.getFinalDuration());
    }

    @Test
    void returnsInvalidForDoubleIn() {
        LocalDate date = LocalDate.of(2026, 3, 24);
        when(repository.findByEmployeeIdAndEventTimeBetween(
                "EMP001", date.atStartOfDay(), date.atTime(23, 59, 59)
        )).thenReturn(new ArrayList<>(List.of(
                event("EMP001", "IN", date.atTime(9, 0)),
                event("EMP001", "IN", date.atTime(10, 0))
        )));

        AttendanceResult result = service.processDay("EMP001", date);

        assertEquals(AttendanceStatus.INVALID, result.getStatus());
        assertEquals("Double IN detected", result.getInvalidReason());
    }

    @Test
    void returnsInvalidForOutBeforeIn() {
        LocalDate date = LocalDate.of(2026, 3, 24);
        when(repository.findByEmployeeIdAndEventTimeBetween(
                "EMP001", date.atStartOfDay(), date.atTime(23, 59, 59)
        )).thenReturn(new ArrayList<>(List.of(
                event("EMP001", "OUT", date.atTime(9, 0))
        )));

        AttendanceResult result = service.processDay("EMP001", date);

        assertEquals(AttendanceStatus.INVALID, result.getStatus());
        assertEquals("OUT before IN detected", result.getInvalidReason());
    }

    @Test
    void returnsInOfficeForCurrentDayOpenSession() {
        LocalDate date = LocalDate.now();
        when(repository.findByEmployeeIdAndEventTimeBetween(
                "EMP001", date.atStartOfDay(), date.atTime(23, 59, 59)
        )).thenReturn(new ArrayList<>(List.of(
                event("EMP001", "IN", date.atTime(9, 0))
        )));

        AttendanceResult result = service.processDay("EMP001", date);

        assertEquals(AttendanceStatus.IN_OFFICE, result.getStatus());
    }

    @Test
    void returnsIncompleteForPastDayOpenSession() {
        LocalDate date = LocalDate.now().minusDays(1);
        when(repository.findByEmployeeIdAndEventTimeBetween(
                "EMP001", date.atStartOfDay(), date.atTime(23, 59, 59)
        )).thenReturn(new ArrayList<>(List.of(
                event("EMP001", "IN", date.atTime(9, 0))
        )));

        AttendanceResult result = service.processDay("EMP001", date);

        assertEquals(AttendanceStatus.INCOMPLETE, result.getStatus());
        assertEquals("Did not tap OUT", result.getInvalidReason());
    }

    private AttendanceEvent event(String employeeId, String type, LocalDateTime time) {
        AttendanceEvent event = new AttendanceEvent();
        event.setEmployeeId(employeeId);
        event.setEventType(type);
        event.setEventTime(time);
        return event;
    }
}
