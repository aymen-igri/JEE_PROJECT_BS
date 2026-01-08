package com.backend.backend.dto.request.Consultation;

import com.backend.backend.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing diagnostic.
 * Consultation cannot be changed - only content fields.
 */
public record UpdateDiagnosticRequest(

        @NotBlank(message = "Diagnosis is required")
        @Size(max = 5000, message = "Diagnosis cannot exceed 5000 characters")
        String diagnosis,

        @Size(max = 20, message = "Diagnosis code cannot exceed 20 characters")
        String diagnosisCode,

        Severity severity,

        @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
        String notes
) {}

