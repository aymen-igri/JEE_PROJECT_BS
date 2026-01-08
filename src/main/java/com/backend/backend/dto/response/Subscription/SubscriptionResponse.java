package com.backend.backend.dto.response.Subscription;

import com.backend.backend.dto.response.Cabinet.CabinetResponse;
import com.backend.backend.entity.subscription.SubscriptionPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        CabinetResponse cabinet,
        SubscriptionPlanResponse subscriptionPlan,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String status,
        Boolean autoRenew,
        LocalDate lastPaymentDate,
        LocalDate nextPaymentDate,
        LocalDate gracePeriodEndDate,
        LocalDate cancelledAt,
        UUID cancelledBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
