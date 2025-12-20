package com.backend.backend.dto.request.Admin;

import com.backend.backend.enums.EGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AdminRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 100)
        String fullName,

        @NotBlank(message = "CIN is required")
        @Size(min = 6)
        String CIN,

        @NotNull(message = "Date of birth is required")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        EGender gender,

        @NotBlank(message = "Address is required")
        @Size(min = 5)
        String address,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required")
        @Size(min = 9, max = 9)
        String phone
) {
}

