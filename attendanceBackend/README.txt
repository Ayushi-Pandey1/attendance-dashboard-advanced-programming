Open in IntelliJ as Maven project.

1. Create PostgreSQL DB called 'attendance'.
2. Update username/password if needed.
3. Run AttendanceApplication.
4. Test endpoint:

POST http://localhost:8080/api/tap

Body:
{
  "employeeId": "EMP001",
  "eventType": "IN"
}
