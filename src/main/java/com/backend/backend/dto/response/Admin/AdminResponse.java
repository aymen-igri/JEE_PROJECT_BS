package com.backend.backend.dto.response.Admin;

import com.backend.backend.enums.EGender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminResponse(
        UUID id,
        String fullName,
        String CIN,
        LocalDate dateOfBirth,
        EGender gender,
        String address,
        String email,
        String phone,
        LocalDateTime createdAt,
        String profilePhoto
) {
}
