package com.backend.backend.dto.response.Doctor;

import com.backend.backend.enums.EGender;
import com.backend.backend.enums.EStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DoctorResponce(
    UUID id,
    String fullName,
    String CIN,
    LocalDate dateOfBirth,
    LocalDateTime createdAt,
    EGender gender,
    String email,
    String address,
    String phone,
    String specialty,
    String licenseNumber,
    String profilePhoto,
    EStatus status
) {
}
