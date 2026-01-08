package com.backend.backend.entity.patient;
import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.enums.Severity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Diagnostic entity - represents a diagnosis for a consultation.
 * Many diagnostics can belong to one consultation.
 * Each diagnostic has its own date since consultations can span multiple appointments.
 */
@Entity
@Table(name = "diagnostics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Diagnostic extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "diagnosis_id")
    private UUID diagnosisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    /**
     * Date when this diagnosis was made.
     */
    @Column(name = "diagnosis_date", nullable = false)
    private LocalDate diagnosisDate;

    @Column(name = "diagnosis", nullable = false, columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "diagnosis_code", length = 20)
    private String diagnosisCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 20)
    private Severity severity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // createdAt and modifiedAt (updatedAt) inherited from AuditableEntity
}
