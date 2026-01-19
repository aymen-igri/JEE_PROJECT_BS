package com.backend.backend.dto.response.Consultation;

import java.util.UUID;

public record ConsultationCardDTO(
        UUID consultationId,
        String patientName,
        String dateOfBirth,
        String sex,
        String phone,
        String notes,
        String status
) {}