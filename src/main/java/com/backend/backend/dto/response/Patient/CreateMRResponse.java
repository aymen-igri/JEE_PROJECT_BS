package com.backend.backend.dto.response.Patient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateMRResponse(

        UUID recordId,
        UUID patientID,
        List<String> allergies,
        String bloodType,
        String chronicDiseases,
        String familyHistory,
        List<String> pastSurgeries,
        LocalDateTime createdAt
) {
}
