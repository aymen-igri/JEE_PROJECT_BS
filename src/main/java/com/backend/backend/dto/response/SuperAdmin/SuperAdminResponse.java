package com.backend.backend.dto.response.SuperAdmin;

import com.backend.backend.enums.EGender;
import com.backend.backend.enums.EStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SuperAdminResponse(
        UUID userId,
        Integer level,
        String CIN,
        String address,
        LocalDate dateOfBirth,
        String fullName,
        EGender gender,
        String phone,
        EStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
