package attendance.service;

import attendance.model.AttendanceStatus;

/**
 * Immutable value object holding the computed attendance outcome for one employee/day.
 * Use the static factory methods to create instances.
 */
public class AttendanceResult {

    private AttendanceStatus status;
    private String finalDuration;
    private String invalidReason;

    private AttendanceResult() {}

    /** Creates a result for a completed day with all tap pairs matched. */
    public static AttendanceResult completed(long minutes, AttendanceStatus status) {
        AttendanceResult r = new AttendanceResult();
        r.status        = status;
        r.finalDuration = format(minutes);
        return r;
    }

    /** Creates a result for an employee currently inside the building. Duration is still live. */
    public static AttendanceResult inOffice(long minutes) {
        AttendanceResult r = new AttendanceResult();
        r.status = AttendanceStatus.IN_OFFICE;
        return r;
    }

    /** Creates a result for a past day where no tap-out was recorded. */
    public static AttendanceResult incomplete(long minutes) {
        AttendanceResult r = new AttendanceResult();
        r.status        = AttendanceStatus.INCOMPLETE;
        r.finalDuration = format(minutes);
        r.invalidReason = "Did not tap OUT";
        return r;
    }

    /** Creates a result for a day with a logically inconsistent tap sequence. */
    public static AttendanceResult invalid(String reason) {
        AttendanceResult r = new AttendanceResult();
        r.status        = AttendanceStatus.INVALID;
        r.invalidReason = reason;
        return r;
    }

    /** Formats raw minutes into a human-readable string, e.g. "3 hours 45 minutes". */
    private static String format(long minutes) {
        long hours     = minutes / 60;
        long remaining = minutes % 60;
        return hours + " hours " + remaining + " minutes";
    }

    public AttendanceStatus getStatus()        { return status; }
    public String           getFinalDuration() { return finalDuration; }
    public String           getInvalidReason() { return invalidReason; }
}
