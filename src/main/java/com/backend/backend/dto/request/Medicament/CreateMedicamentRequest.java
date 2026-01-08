package com.backend.backend.dto.request.Medicament;

import com.backend.backend.enums.MedicamentForm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new medicament.
 * Only admins and super admins can create medicaments.
 */
public record CreateMedicamentRequest(
        @NotBlank(message = "Medicament name is required")
        @Size(max = 200, message = "Name cannot exceed 200 characters")
        String name,

        @Size(max = 50, message = "Dosage cannot exceed 50 characters")
        String dosage,

        @NotNull(message = "Form is required")
        MedicamentForm form,

        @Size(max = 100, message = "Category cannot exceed 100 characters")
        String category
) {}

