package com.backend.backend.dto.request.Consultation;

import com.backend.backend.enums.ConsultationStatus;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for updating an existing consultation.
 * Does not include IDs - those cannot be changed.
 * Consultations have no date or price - billing is per appointment.
 */
public record UpdateConsultationRequest(

        @Size(max = 1000, message = "Chief complaint cannot exceed 1000 characters")
        String chiefComplaint,

        @Size(max = 2000, message = "Symptoms cannot exceed 2000 characters")
        String symptoms,

        List<String> vitalSigns,

        @Size(max = 2000, message = "Physical exam cannot exceed 2000 characters")
        String physicalExam,

        @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
        String notes,


        ConsultationStatus status
) {}

