export type AttendanceStatus =
  | 'IN_OFFICE'
  | 'PRESENT'
  | 'PARTIAL'
  | 'INCOMPLETE'
  | 'INVALID'
  | 'NO_DATA';

export interface TodayStatus {
  employeeId: string;
  status: AttendanceStatus;
  lastInTime: string | null;   // ISO datetime string
  duration: string | null;
}

export interface DailyRecord {
  attendanceDate: string;       // "YYYY-MM-DD"
  status: AttendanceStatus;
  finalInOfficeDuration: string | null;
  invalidReason: string | null;
}
