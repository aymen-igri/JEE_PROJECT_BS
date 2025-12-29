package com.backend.backend.dto.request.Patient;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record DoctorPatientLinkRequest(

        @NotNull(message = "Doctor ID is required")
        UUID doctorId,

        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Secretary ID is required")
        UUID secretaryId
) {
}
