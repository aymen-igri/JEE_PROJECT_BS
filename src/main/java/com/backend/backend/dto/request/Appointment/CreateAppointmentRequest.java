package com.backend.backend.dto.request.Appointment;

import com.backend.backend.enums.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(

        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Doctor ID is required")
        UUID doctorId,

        @NotNull(message = "Cabinet ID is required")
        UUID cabinetId,

        @NotNull(message = "Appointment date and time is required")
        @Future(message = "Appointment must be scheduled in the future")
        LocalDateTime appointmentDateTime,

        @NotNull(message = "Duration is required")
        @Min(value = 15, message = "Minimum duration is 15 minutes")
        @Max(value = 120, message = "Maximum duration is 120 minutes")
        Integer duration,

        @NotNull(message = "Appointment type is required")
        AppointmentType appointmentType,

        @Size(max = 500, message = "Reason cannot exceed 500 characters")
        String reason,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes
) {}
