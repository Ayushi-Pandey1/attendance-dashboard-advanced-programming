package attendance.model;


public enum AttendanceStatus {
 /** In office for 4+ hours with matched tap-out. */
 PRESENT,
 /** In office but fewer than 4 hours. */
 PARTIAL,
 /** Currently inside the building — session still live. */
 IN_OFFICE,
 /** Day ended without a tap-out. */
 INCOMPLETE,
 /** Tap sequence was logically inconsistent (e.g. double-IN). */
 INVALID
}