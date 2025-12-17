package com.backend.backend.dto.response.Doctor;

import com.backend.backend.enums.ApplicationStatus;

import java.time.LocalDate;
import java.util.UUID;

public record DoctorAppResponce(

        UUID id,
        String fullName,
        String email,
        String CIN,
        String phone,
        String specialty,
        String licenseNumber,
        String diplomaDocument,
        String licenseDocument,
        String cvDocument,
        ApplicationStatus status,
        LocalDate applicationDate
){}
