package com.backend.backend.dto.request.Consultation;

import com.backend.backend.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating a new diagnostic.
 * Each diagnostic has its own date since consultations can span multiple appointments.
 */
public record CreateDiagnosticRequest(

        @NotNull(message = "Consultation ID is required")
        UUID consultationId,

        /**
         * Date when this diagnosis is made.
         * If not provided, defaults to current date.
         */
        LocalDate diagnosisDate,

        @NotBlank(message = "Diagnosis is required")
        @Size(max = 5000, message = "Diagnosis cannot exceed 5000 characters")
        String diagnosis,

        @Size(max = 20, message = "Diagnosis code cannot exceed 20 characters")
        String diagnosisCode,

        Severity severity,

        @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
        String notes
) {}

