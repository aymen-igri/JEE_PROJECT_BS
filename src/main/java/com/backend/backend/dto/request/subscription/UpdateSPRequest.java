package com.backend.backend.dto.request.subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UpdateSPRequest(
        UUID id,
        String planName,
        BigDecimal price,
        UUID createdBy,
        String billingCycle,
        Integer maxDoctors,
        Integer maxSecretary,
        List<String> features,
        Boolean isActive,
        LocalDateTime updatedAt
) {
}
