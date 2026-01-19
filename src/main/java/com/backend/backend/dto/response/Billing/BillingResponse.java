package com.backend.backend.dto.response.Billing;

import com.backend.backend.enums.PaymentStatus;
import com.backend.backend.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for billing/receipt data.
 * Billing is per appointment, not per consultation.
 */
public record BillingResponse(
        UUID billingId,
        String receiptNumber,
        UUID appointmentId,
        LocalDateTime appointmentDateTime,
        String patientName,
        String patientCin,
        String doctorName,
        String cabinetName,
        BigDecimal originalPrice,
        BigDecimal discountAmount,
        String discountReason,
        BigDecimal finalAmount,
        PaymentType paymentType,
        PaymentStatus paymentStatus,
        LocalDateTime paymentDate,
        String processedByName,
        String notes,
        String pdfPath,
        LocalDateTime createdAt
) {}
