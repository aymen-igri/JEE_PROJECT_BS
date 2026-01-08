package com.backend.backend.dto.response.Consultation;

import java.util.UUID;

/**
 * Response DTO for analysis/examination data.
 */
public record AnalysisResponse(
        UUID analysisId,
        String analysisType,
        String analysisName,
        String instructions,
        String results,
        String resultFilePath
) {}

