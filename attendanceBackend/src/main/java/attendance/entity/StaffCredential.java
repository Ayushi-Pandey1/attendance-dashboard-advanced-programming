package attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "staff_credentials")
public class StaffCredential {

    /** Unique staff identifier. */
    @Id
    @Column(name = "staff_number")
    private String staffNumber;

    /** BCrypt hash of the staff member's password. */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** When this credential record was created. */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public String        getStaffNumber()              { return staffNumber; }
    public void          setStaffNumber(String v)      { this.staffNumber = v; }
    public String        getPasswordHash()             { return passwordHash; }
    public void          setPasswordHash(String v)     { this.passwordHash = v; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void          setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
