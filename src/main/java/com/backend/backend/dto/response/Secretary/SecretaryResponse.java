package com.backend.backend.dto.response.Secretary;

import com.backend.backend.enums.EGender;

import java.time.LocalDate;
import java.util.UUID;

public record SecretaryResponse (

        UUID id,
        String fullName,
        String CIN,
        LocalDate dateOfBirth,
        EGender gender,
        String address,
        String email,
        String phone,
        String profilePhoto
) {}
