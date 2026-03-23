package attendance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AttendanceApplication {

 private static final Logger log = LoggerFactory.getLogger(AttendanceApplication.class);

 public static void main(String[] args) {
  log.info("Starting Attendance Tracking application...");
  SpringApplication.run(AttendanceApplication.class, args);
  log.info("Attendance Tracking application started successfully.");
 }
}
