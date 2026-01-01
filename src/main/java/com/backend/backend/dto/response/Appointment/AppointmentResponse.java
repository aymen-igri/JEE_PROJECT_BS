package com.backend.backend.dto.response.Appointment;

import com.backend.backend.enums.AppointmentStatus;
import com.backend.backend.enums.AppointmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID appointmentId,
        UUID patientId,
        String patientName,
        UUID doctorId,
        String doctorName,
        UUID cabinetId,
        String cabinetName,
        LocalDateTime appointmentDateTime,
        Integer duration,
        AppointmentType appointmentType,
        AppointmentStatus status,
        String reason,
        String notes,
        UUID scheduledBySecretaryId,
        String scheduledBySecretaryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
