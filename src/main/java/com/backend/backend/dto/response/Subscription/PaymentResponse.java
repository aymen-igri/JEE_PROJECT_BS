package com.backend.backend.dto.response.Subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        SubscriptionResponse subscription,
        BigDecimal amount,
        String paymentType,
        String transactionId,
        String status,
        Integer paidBy,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
