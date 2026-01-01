package com.backend.backend.dto.request.Appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record RescheduleAppointmentRequest(

        @NotNull(message = "Appointment ID is required")
        UUID appointmentId,

        @NotNull(message = "New date and time is required")
        @Future(message = "New appointment time must be in the future")
        LocalDateTime newDateTime,

        @Min(value = 15, message = "Minimum duration is 15 minutes")
        @Max(value = 120, message = "Maximum duration is 120 minutes")
        Integer newDuration,

        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reason
) {}

