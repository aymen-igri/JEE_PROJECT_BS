package com.backend.backend.dto.response.Appointment;

import com.backend.backend.enums.AppointmentStatus;
import com.backend.backend.enums.AppointmentType;
import com.backend.backend.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for appointment data.
 * Billing is per appointment, not per consultation.
 * An appointment can optionally be linked to a consultation.
 */
public record AppointmentResponse(
        UUID appointmentId,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        UUID cabinetId,
        String cabinetName,
        UUID consultationId,  // Optional link to consultation
        LocalDateTime appointmentDateTime,
        Integer duration,
        AppointmentType appointmentType,
        AppointmentStatus status,
        String reason,
        String notes,
        BigDecimal price,
        PaymentStatus paymentStatus,
        UUID scheduledBySecretaryId,
        String scheduledBySecretaryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
