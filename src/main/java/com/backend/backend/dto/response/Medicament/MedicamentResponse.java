package com.backend.backend.dto.response.Medicament;

import com.backend.backend.enums.MedicamentForm;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for medicament data.
 */
public record MedicamentResponse(
        UUID medicamentId,
        String name,
        String dosage,
        MedicamentForm form,
        String category,
        Boolean isActive,
        UUID createdById,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

