package com.backend.backend.dto.response.Consultation;

import com.backend.backend.enums.ConsultationStatus;
import com.backend.backend.enums.ConsultationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Detailed response DTO for consultation with nested diagnostics and prescriptions.
 * Consultations have no date - they are tracked only by status.
 * Use for single consultation view.
 */
public record ConsultationDetailResponse(
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
        List<DiagnosticResponse> diagnostics,
        List<PrescriptionResponse> prescriptions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

