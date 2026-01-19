package com.backend.backend.dto.request.Consultation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO for a single prescription item (nested in CreatePrescriptionRequest).
 */
public record PrescriptionItemRequest(

        @NotNull(message = "Medicament ID is required")
        UUID medicamentId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @Size(max = 100, message = "Frequency cannot exceed 100 characters")
        String frequency,

        @Size(max = 50, message = "Duration cannot exceed 50 characters")
        String duration,

        @Size(max = 500, message = "Instructions cannot exceed 500 characters")
        String instructions
) {}

