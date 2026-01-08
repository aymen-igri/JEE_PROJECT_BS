package com.backend.backend.entity.patient;

import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.enums.PaymentStatus;
import com.backend.backend.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a billing/receipt for an appointment.
 * Billing is per appointment, not per consultation.
 * Created by secretary after appointment payment is received.
 * When billing is processed, the appointment is automatically marked as COMPLETED.
 */
@Entity
@Table(name = "appointment_billings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentBilling extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "billing_id")
    private UUID billingId;

    /**
     * The appointment being billed.
     * Billing is per appointment.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by", nullable = false)
    private Secretary processedBy;

    @Column(name = "receipt_number", unique = true, nullable = false, length = 50)
    private String receiptNumber;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "discount_reason", length = 255)
    private String discountReason;

    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 30)
    @Builder.Default
    private PaymentType paymentType = PaymentType.CASH;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    // createdAt and updatedAt inherited from AuditableEntity
}

