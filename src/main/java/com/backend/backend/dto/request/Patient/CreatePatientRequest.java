package com.backend.backend.dto.request.Patient;

import com.backend.backend.enums.EGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePatientRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 5, max = 50)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 5, max = 50)
        String lastName,

        @NotBlank(message = "phone name is required")
        @Size(min = 9, max = 30)
        String CIN,

        LocalDate dateOfBirth,

        @NotNull(message = "Gender is required")
        EGender gender,

        @NotBlank(message = "phone name is required")
        @Size(min = 9, max = 9)
        String phone,

        String address,

        @NotNull(message = "Secretary UUID is required")
        UUID registedBY
) {
}
