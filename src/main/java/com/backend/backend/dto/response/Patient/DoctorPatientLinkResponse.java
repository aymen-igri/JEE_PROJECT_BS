package com.backend.backend.dto.response.Patient;

import java.time.LocalDate;
import java.util.UUID;

public record DoctorPatientLinkResponse(

        UUID id,
        UUID doctorId,
        UUID patientId,
        UUID secretaryId,
        String status,
        LocalDate linkedAt
) {
}
