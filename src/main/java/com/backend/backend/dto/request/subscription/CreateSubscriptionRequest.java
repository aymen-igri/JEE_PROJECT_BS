package com.backend.backend.dto.request.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {

    @NotNull(message = "Cabinet ID is required")
    private UUID cabinetId;

    @NotNull(message = "Plan ID is required")
    private UUID planId;

    private Boolean autoRenew = false;
}