package com.backend.backend.dto.request.SuperAdmin;

import com.backend.backend.enums.EGender;

import java.time.LocalDate;

public record SuperAdminRequest(
        String CIN,
        String address,
        LocalDate dateOfBirth,
        String fullName,
        EGender gender,
        String phone
) {
}
