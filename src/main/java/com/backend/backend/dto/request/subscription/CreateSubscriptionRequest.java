package com.backend.backend.dto.request.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {

    @NotBlank(message = "Plan ID is required")
    private String planId;  // Changed to String: "casual", "pro", or "pro-plus"

    @NotNull(message = "Auto-renew setting is required")
    private Boolean autoRenew = false;
}