package attendance.repository;

import attendance.entity.DailyAttendanceEntity;
import attendance.model.AttendanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class DailyAttendanceRepositoryTest {

    @Autowired
    private DailyAttendanceRepository repository;

    @Test
    void findsRecordByEmployeeAndDate() {
        DailyAttendanceEntity entity = new DailyAttendanceEntity("EMP001", LocalDate.of(2026, 3, 24));
        entity.setStatus(AttendanceStatus.PRESENT);
        repository.save(entity);

        Optional<DailyAttendanceEntity> found =
                repository.findByEmployeeIdAndAttendanceDate("EMP001", LocalDate.of(2026, 3, 24));

        assertTrue(found.isPresent());
        assertEquals(AttendanceStatus.PRESENT, found.get().getStatus());
    }

    @Test
    void returnsHistoryInDescendingDateOrder() {
        DailyAttendanceEntity older = new DailyAttendanceEntity("EMP001", LocalDate.of(2026, 3, 20));
        older.setStatus(AttendanceStatus.PARTIAL);
        DailyAttendanceEntity newer = new DailyAttendanceEntity("EMP001", LocalDate.of(2026, 3, 24));
        newer.setStatus(AttendanceStatus.PRESENT);
        repository.saveAll(List.of(older, newer));

        List<DailyAttendanceEntity> history = repository.findByEmployeeIdOrderByAttendanceDateDesc("EMP001");

        assertEquals(2, history.size());
        assertEquals(LocalDate.of(2026, 3, 24), history.get(0).getAttendanceDate());
        assertEquals(LocalDate.of(2026, 3, 20), history.get(1).getAttendanceDate());
    }

    @Test
    void returnsRecordsWithinMonthRange() {
        DailyAttendanceEntity marchRecord = new DailyAttendanceEntity("EMP001", LocalDate.of(2026, 3, 12));
        marchRecord.setStatus(AttendanceStatus.PRESENT);
        DailyAttendanceEntity aprilRecord = new DailyAttendanceEntity("EMP001", LocalDate.of(2026, 4, 1));
        aprilRecord.setStatus(AttendanceStatus.PRESENT);
        repository.saveAll(List.of(marchRecord, aprilRecord));

        List<DailyAttendanceEntity> results =
                repository.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        "EMP001",
                        LocalDate.of(2026, 3, 1),
                        LocalDate.of(2026, 3, 31)
                );

        assertEquals(1, results.size());
        assertEquals(LocalDate.of(2026, 3, 12), results.get(0).getAttendanceDate());
    }
}
