package com.backend.backend.dto.request.Admin;

import com.backend.backend.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AdminAccResp(

        @NotNull(message = "ProcessedBy is required")
        UUID processedBy,

        @NotNull(message = "The new status is required")
        ApplicationStatus status,

        String rejectionReason,

        String note
) {
}
