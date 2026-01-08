package com.backend.backend.dto.response.Consultation;

import com.backend.backend.enums.ConsultationStatus;
import com.backend.backend.enums.ConsultationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for consultation data.
 * Consultations have no date - they are tracked only by status.
 * Includes denormalized names for display.
 */
public record ConsultationResponse(
        UUID consultationId,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        ConsultationType consultationType,
        String chiefComplaint,
        String symptoms,
        List<String> vitalSigns,
        String physicalExam,
        String notes,
        ConsultationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

