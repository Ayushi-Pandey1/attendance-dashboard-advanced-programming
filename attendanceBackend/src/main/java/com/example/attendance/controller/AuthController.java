package com.example.attendance.controller;

import com.example.attendance.dto.LoginRequestDto;
import com.example.attendance.entity.StaffCredential;
import com.example.attendance.repository.StaffCredentialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {

    private final StaffCredentialRepository credentialRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(StaffCredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        String staffNumber = request.getStaffNumber().trim().toUpperCase();

        Optional<StaffCredential> credOpt = credentialRepository.findById(staffNumber);

        if (credOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid staff number or password"));
        }

        boolean matches = passwordEncoder.matches(request.getPassword(), credOpt.get().getPasswordHash());

        if (!matches) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid staff number or password"));
        }

        return ResponseEntity.ok(Map.of(
                "staffNumber", staffNumber,
                "message", "Login successful"
        ));
    }
}