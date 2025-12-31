package com.backend.backend.dto.request.Doctor;

import com.backend.backend.enums.EStatus;
import com.backend.backend.enums.EGender;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DoctorInfoResponse(
        String fullName,
        String email,
        String username,
        String CIN,
        String phone,
        String address,
        LocalDate dateOfBirth,
        EGender gender,
        String specialty,
        String licenseNumber,
        EStatus status,
        String profilePhoto,
        LocalDateTime createdAt  // from AuditableEntity for join date
) {}