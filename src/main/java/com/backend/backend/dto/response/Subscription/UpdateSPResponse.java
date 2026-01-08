package com.backend.backend.dto.response.Subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UpdateSPResponse(
        UUID id,
        String planName,
        BigDecimal price,
        UUID createdBy,
        String billingCycle,
        Integer maxDoctors,
        Integer maxSecretary,
        List<String> features,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
