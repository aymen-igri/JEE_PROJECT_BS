package com.backend.backend.dto.response.Consultation;

import com.backend.backend.enums.Severity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for diagnostic data.
 * Each diagnostic has its own date since consultations can span multiple appointments.
 * Includes canModify flag computed from grace period.
 */
public record DiagnosticResponse(
        UUID diagnosisId,
        UUID consultationId,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        LocalDate diagnosisDate,
        String diagnosis,
        String diagnosisCode,
        Severity severity,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean canModify  // Computed: true if within grace period
) {}

