package com.backend.backend.dto.response.Consultation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for prescription data.
 * Includes nested items and analyses.
 */
public record PrescriptionResponse(
        UUID prescriptionId,
        UUID consultationId,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        String dosage,
        String frequency,
        String duration,
        String instructions,
        LocalDate prescribedDate,
        List<PrescriptionItemResponse> items,
        List<AnalysisResponse> analyses,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

