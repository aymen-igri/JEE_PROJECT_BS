package com.backend.backend.dto.request.Doctor;

import com.backend.backend.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DoctorAppDataRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 50)
        String fullName,

        @NotBlank(message = "email name is required")
        @Size(min = 10, max = 50)
        String email,

        @NotBlank(message = "CIN name is required")
        @Size(min = 3, max = 100)
        String CIN,

        @NotBlank(message = "phone name is required")
        @Size(min = 9, max = 9)
        String phone,

        @NotBlank(message = "specialty name is required")
        @Size(min = 5, max = 30)
        String specialty,

        @NotBlank(message = "license Number name is required")
        @Size(min = 10, max = 30)
        String licenseNumber,

        @NotBlank(message = "Diplomat Document is required")
        String diplomaDocument,

        @NotBlank(message = "license Document is required")
        String licenseDocument,

        @NotBlank(message = "CV Document is required")
        String cvDocument,

        @NotNull(message = "license Number name is required")
        ApplicationStatus status

){}
