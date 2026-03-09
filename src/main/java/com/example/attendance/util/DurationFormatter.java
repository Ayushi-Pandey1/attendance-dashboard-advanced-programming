package com.example.attendance.util;

public class DurationFormatter {

    public static String formatMinutes(long minutes) {

        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        return hours + " hours " + remainingMinutes + " minutes";
    }
}