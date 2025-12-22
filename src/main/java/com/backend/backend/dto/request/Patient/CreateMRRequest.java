package com.backend.backend.dto.request.Patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateMRRequest (

        @NotNull(message = "Patient ID is required")
        UUID patientID,

        List<String> allergies,

        @NotBlank(message = "Blood type is required")
        String bloodType,

        String chronicDiseases,

        @NotBlank(message = "Family history is required")
        String familyHistory,

        List<String> pastSurgeries
) {
}
