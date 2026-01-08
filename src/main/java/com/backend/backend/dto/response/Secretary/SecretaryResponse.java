package com.backend.backend.dto.response.Secretary;

import com.backend.backend.enums.EGender;
import com.backend.backend.enums.EStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SecretaryResponse (
        UUID id,
        String fullName,
        String CIN,
        LocalDate dateOfBirth,
        LocalDateTime createdAt,
        EGender gender,
        String address,
        String email,
        String phone,
        String profilePhoto,
        EStatus status
) {}
