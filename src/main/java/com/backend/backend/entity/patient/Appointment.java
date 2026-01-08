package com.backend.backend.entity.patient;


import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.entity.practice.Cabinet;
import  com.backend.backend.enums.AppointmentType;
import com.backend.backend.enums.AppointmentStatus;
import com.backend.backend.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Appointment entity - represents a scheduled visit.
 * Billing is done per appointment, not per consultation.
 * An appointment can optionally be linked to a consultation.
 * Appointment is marked done by doctor/secretary OR when payment is processed.
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Appointment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "appointment_id")
    private UUID appointmentId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = true, updatable = true)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", insertable = true, updatable = true)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", insertable = true, updatable = true)
    private Cabinet cabinet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_by", insertable = true, updatable = true)
    private Secretary scheduledBySecretary;

    /**
     * Optional link to a consultation.
     * An appointment may or may not be part of a consultation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "duration")
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", length = 30)
    private AppointmentType appointmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Price for this appointment (can be set from cabinet default or custom).
     */
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Payment status for billing.
     * Billing is per appointment.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // createdAt and updatedAt inherited from AuditableEntity
}
