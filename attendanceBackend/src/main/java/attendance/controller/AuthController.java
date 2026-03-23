package attendance.controller;

import attendance.dto.LoginRequestDto;
import attendance.entity.StaffCredential;
import attendance.repository.StaffCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final StaffCredentialRepository credentialRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(StaffCredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    /**
     * Authenticates a staff member using their staff number and password.
     * Returns 200 on success, 401 on failure. Both failure cases return the same
     * error message to prevent user enumeration attacks.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        // Normalise to uppercase — staff numbers are stored in uppercase in the DB
        String staffNumber = request.getStaffNumber().trim().toUpperCase();

        log.info("Login attempt — staffNumber={}", staffNumber);

        Optional<StaffCredential> credOpt = credentialRepository.findById(staffNumber);

        if (credOpt.isEmpty()) {
            log.warn("Login failed — staff number not found: {}", staffNumber);
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid staff number or password"));
        }

        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                credOpt.get().getPasswordHash()
        );

        if (!matches) {
            log.warn("Login failed — incorrect password for staffNumber={}", staffNumber);
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid staff number or password"));
        }

        log.info("Login successful — staffNumber={}", staffNumber);
        return ResponseEntity.ok(Map.of(
                "staffNumber", staffNumber,
                "message",     "Login successful"
        ));
    }
}
