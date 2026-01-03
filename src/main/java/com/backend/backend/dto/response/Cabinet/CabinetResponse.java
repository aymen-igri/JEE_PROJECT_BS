package com.backend.backend.dto.response.Cabinet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CabinetResponse(
        UUID id,
        String name,
        String logo,
        String specialty,
        String description,
        String phone,
        String status,
        BigDecimal defaultConsultPrice,
        UUID createdBy,
        LocalDateTime createdAt
) {
}
