package com.example.attendance.repository;

import com.example.attendance.entity.StaffCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffCredentialRepository extends JpaRepository<StaffCredential, String> {
}