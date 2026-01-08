package com.backend.backend.dto.request.subscription;

import java.math.BigDecimal;
import java.util.List;

public record SubscriptionPlanRequest(
        String planName,
        BigDecimal price,
        String billingCycle,
        Integer maxDoctors,
        Integer maxSecretary,
        List<String> features
) {
}
