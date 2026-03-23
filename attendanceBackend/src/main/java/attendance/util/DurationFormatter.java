package attendance.util;

public class DurationFormatter {

    /** Converts a minute count to a readable string, e.g. 90 → "1 hours 30 minutes". */
    public static String formatMinutes(long minutes) {
        long hours            = minutes / 60;
        long remainingMinutes = minutes % 60;
        return hours + " hours " + remainingMinutes + " minutes";
    }
}