package com.backend.backend.dto.response.Patient;

import com.backend.backend.enums.EGender;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePatientResponse(
        UUID id,
        String firstName,
        String lastName,
        String CIN,
        LocalDate dateOfBirth,
        EGender gender,
        String phone,
        String address,
        LocalDate registrationDate,
        UUID registeredBy
) {
}
