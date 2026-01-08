package com.backend.backend.dto.request.Admin;

import com.backend.backend.enums.EGender;

import java.time.LocalDate;

public record AdminURequest(
        String CIN,
        String address,
        LocalDate dateOfBirth,
        String fullName,
        EGender gender,
        String phone
) {
}
