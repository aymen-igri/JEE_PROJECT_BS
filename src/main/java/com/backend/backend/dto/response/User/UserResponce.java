package com.backend.backend.dto.response.User;

import com.backend.backend.enums.EGender;
import com.backend.backend.enums.EStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponce(
    UUID id,
    String fullName,
    String CIN,
    LocalDate dateOfBirth,
    LocalDateTime createdAt,
    EGender gender,
    String address,
    String email,
    String phone,
    EStatus status,
    String profilePhoto
) {
}
