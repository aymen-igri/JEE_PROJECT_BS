package com.backend.backend.dto.request.Billing;

import com.backend.backend.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for creating a billing/receipt for an appointment.
 * Billing is per appointment, not per consultation.
 * Used by secretary to process payment.
 * When payment is processed, appointment is automatically marked as COMPLETED.
 */
public record CreateBillingRequest(
        @NotNull(message = "Appointment ID is required")
        UUID appointmentId,

        @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
        BigDecimal discountAmount,

        String discountReason,

        @NotNull(message = "Payment type is required")
        PaymentType paymentType,

        String notes
) {}
