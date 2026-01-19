package com.backend.backend.dto.response.Consultation;

import com.backend.backend.enums.MedicamentForm;
import java.util.UUID;

/**
 * Response DTO for prescription item data.
 */
public record PrescriptionItemResponse(
        UUID itemId,
        UUID medicamentId,
        String medicamentName,
        String medicamentDosage,
        MedicamentForm medicamentForm,
        Integer quantity,
        String frequency,
        String duration,
        String instructions
) {}

