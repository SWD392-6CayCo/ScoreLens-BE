package com.scorelens.Repository;

import com.scorelens.Entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, String> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}