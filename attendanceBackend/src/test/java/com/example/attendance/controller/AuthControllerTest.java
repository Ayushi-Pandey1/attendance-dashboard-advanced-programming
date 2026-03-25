package attendance.controller;

import attendance.entity.StaffCredential;
import attendance.repository.StaffCredentialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaffCredentialRepository credentialRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void loginReturnsSuccessForValidCredentials() throws Exception {
        StaffCredential credential = new StaffCredential();
        credential.setStaffNumber("EMP001");
        credential.setPasswordHash(passwordEncoder.encode("secret"));

        when(credentialRepository.findById("EMP001")).thenReturn(Optional.of(credential));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"staffNumber":" emp001 ","password":"secret"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffNumber").value("EMP001"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void loginReturnsUnauthorizedForInvalidPassword() throws Exception {
        StaffCredential credential = new StaffCredential();
        credential.setStaffNumber("EMP001");
        credential.setPasswordHash(passwordEncoder.encode("secret"));

        when(credentialRepository.findById("EMP001")).thenReturn(Optional.of(credential));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"staffNumber":"EMP001","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid staff number or password"));
    }

    @Test
    void loginReturnsUnauthorizedWhenStaffNumberDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"staffNumber":"EMP999","password":"secret"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid staff number or password"));
    }
}
