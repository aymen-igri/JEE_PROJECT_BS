package com.backend.backend.dto.request.Medicament;

import com.backend.backend.enums.MedicamentForm;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a medicament.
 * Only admins and super admins can update medicaments.
 */
public record UpdateMedicamentRequest(
        @Size(max = 200, message = "Name cannot exceed 200 characters")
        String name,

        @Size(max = 50, message = "Dosage cannot exceed 50 characters")
        String dosage,

        MedicamentForm form,

        @Size(max = 100, message = "Category cannot exceed 100 characters")
        String category,

        Boolean isActive
) {}

