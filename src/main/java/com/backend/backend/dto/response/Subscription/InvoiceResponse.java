package com.backend.backend.dto.response.Subscription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        PaymentResponse payment,
        String invoiceNumber,
        LocalDate issueDate,
        LocalDate dueDate,
        BigDecimal totalAmount,
        BigDecimal taxAmount,
        String status,
        String pdfPath
) {
}
