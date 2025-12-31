package com.backend.backend.dto.request.Appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CancelAppointmentRequest(

        @NotNull(message = "Appointment ID is required")
        UUID appointmentId,

        @NotBlank(message = "Cancellation reason is required")
        @Size(min = 10, max = 500, message = "Cancellation reason must be between 10 and 500 characters")
        String cancellationReason
) {}
