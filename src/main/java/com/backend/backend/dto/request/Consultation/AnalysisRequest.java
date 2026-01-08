package com.backend.backend.dto.request.Consultation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for a single analysis/examination (nested in CreatePrescriptionRequest).
 */
public record AnalysisRequest(

        @NotBlank(message = "Analysis type is required")
        @Size(max = 100, message = "Analysis type cannot exceed 100 characters")
        String analysisType,

        @NotBlank(message = "Analysis name is required")
        @Size(max = 200, message = "Analysis name cannot exceed 200 characters")
        String analysisName,

        @Size(max = 1000, message = "Instructions cannot exceed 1000 characters")
        String instructions
) {}

