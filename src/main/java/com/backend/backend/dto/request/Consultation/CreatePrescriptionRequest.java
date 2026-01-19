package com.backend.backend.dto.request.Consultation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new prescription.
 * Includes nested prescription items.
 */
public record CreatePrescriptionRequest(

        @NotNull(message = "Consultation ID is required")
        UUID consultationId,

        @Size(max = 100, message = "Dosage cannot exceed 100 characters")
        String dosage,

        @Size(max = 100, message = "Frequency cannot exceed 100 characters")
        String frequency,

        @Size(max = 50, message = "Duration cannot exceed 50 characters")
        String duration,

        @Size(max = 1000, message = "Instructions cannot exceed 1000 characters")
        String instructions,

        @Valid
        List<PrescriptionItemRequest> items,

        @Valid
        List<AnalysisRequest> analyses
) {}

