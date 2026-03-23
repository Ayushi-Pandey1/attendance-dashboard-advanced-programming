package attendance.repository;

import attendance.entity.StaffCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffCredentialRepository extends JpaRepository<StaffCredential, String> {
}