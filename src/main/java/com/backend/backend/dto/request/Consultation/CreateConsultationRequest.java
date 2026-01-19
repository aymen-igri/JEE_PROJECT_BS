package com.backend.backend.dto.request.Consultation;

import com.backend.backend.enums.ConsultationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating a new consultation.
 * Consultations are created via patient ID.
 * Consultations have no date - they are tracked only by status.
 * Note: doctorId is validated against authenticated user in service layer.
 */
public record CreateConsultationRequest(

        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Consultation type is required")
        ConsultationType consultationType,

        @Size(max = 1000, message = "Chief complaint cannot exceed 1000 characters")
        String chiefComplaint,

        @Size(max = 2000, message = "Symptoms cannot exceed 2000 characters")
        String symptoms,

        List<String> vitalSigns,

        @Size(max = 2000, message = "Physical exam cannot exceed 2000 characters")
        String physicalExam,

        @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
        String notes
) {}

