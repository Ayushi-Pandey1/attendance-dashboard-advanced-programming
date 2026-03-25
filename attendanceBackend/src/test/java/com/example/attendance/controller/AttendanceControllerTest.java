package attendance.controller;

import attendance.entity.AttendanceEvent;
import attendance.entity.DailyAttendanceEntity;
import attendance.model.AttendanceStatus;
import attendance.repository.AttendanceEventRepository;
import attendance.repository.DailyAttendanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DailyAttendanceRepository dailyRepo;

    @MockBean
    private AttendanceEventRepository eventRepo;

    @Test
    void tapStoresValidAttendanceEvent() throws Exception {
        mockMvc.perform(post("/api/attendance/tap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId":" emp001 ","eventType":"in"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Event recorded"));

        verify(eventRepo).save(any(AttendanceEvent.class));
    }

    @Test
    void tapAcceptsEmployeeIdsWithoutServerSidePrefixValidation() throws Exception {
        mockMvc.perform(post("/api/attendance/tap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId":"mgr001","eventType":"IN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Event recorded"));
    }

    @Test
    void tapUppercasesEventTypeBeforeSaving() throws Exception {
        mockMvc.perform(post("/api/attendance/tap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employeeId":"EMP001","eventType":"START"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Event recorded"));

        verify(eventRepo).save(any(AttendanceEvent.class));
    }

    @Test
    void todayReturnsNoDataWhenAttendanceMissing() throws Exception {
        when(dailyRepo.findByEmployeeIdAndAttendanceDate("EMP001", LocalDate.now()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/attendance/today/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_DATA"));
    }

    @Test
    void todayReturnsLastInTimeForInOfficeStatus() throws Exception {
        LocalDate today = LocalDate.now();
        DailyAttendanceEntity entity = new DailyAttendanceEntity("EMP001", today);
        entity.setStatus(AttendanceStatus.IN_OFFICE);
        entity.setFinalInOfficeDuration("ONGOING");

        when(dailyRepo.findByEmployeeIdAndAttendanceDate("EMP001", today))
                .thenReturn(Optional.of(entity));
        when(eventRepo.findByEmployeeIdAndEventTimeBetweenOrderByEventTimeAsc(
                "EMP001", today.atStartOfDay(), today.atTime(23, 59, 59)
        )).thenReturn(List.of(
                event("EMP001", "IN", today.atTime(9, 0)),
                event("EMP001", "OUT", today.atTime(12, 0)),
                event("EMP001", "IN", today.atTime(13, 0))
        ));

        mockMvc.perform(get("/api/attendance/today/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_OFFICE"))
                .andExpect(jsonPath("$.lastInTime").value(
                        today.atTime(13, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                ));
    }

    @Test
    void historyReturnsAttendanceRecords() throws Exception {
        DailyAttendanceEntity entity = new DailyAttendanceEntity("EMP001", LocalDate.of(2026, 3, 24));
        entity.setStatus(AttendanceStatus.PRESENT);
        entity.setFinalInOfficeDuration("4 hours 0 minutes");

        when(dailyRepo.findByEmployeeIdOrderByAttendanceDateDesc("EMP001"))
                .thenReturn(List.of(entity));

        mockMvc.perform(get("/api/attendance/history/EMP001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendanceDate").value("2026-03-24"))
                .andExpect(jsonPath("$[0].status").value("PRESENT"))
                .andExpect(jsonPath("$[0].finalInOfficeDuration").value("4 hours 0 minutes"));
    }

    @Test
    void monthReturnsRecordsWithinRange() throws Exception {
        DailyAttendanceEntity entity = new DailyAttendanceEntity("EMP001", LocalDate.of(2026, 3, 10));
        entity.setStatus(AttendanceStatus.PARTIAL);
        entity.setFinalInOfficeDuration("2 hours 0 minutes");

        when(dailyRepo.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                "EMP001",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 31)
        )).thenReturn(List.of(entity));

        mockMvc.perform(get("/api/attendance/month/EMP001")
                        .param("year", "2026")
                        .param("month", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attendanceDate").value("2026-03-10"))
                .andExpect(jsonPath("$[0].status").value("PARTIAL"));
    }

    private AttendanceEvent event(String employeeId, String type, LocalDateTime time) {
        AttendanceEvent event = new AttendanceEvent();
        event.setEmployeeId(employeeId);
        event.setEventType(type);
        event.setEventTime(time);
        return event;
    }
}
