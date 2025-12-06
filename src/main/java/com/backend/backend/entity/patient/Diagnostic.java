package com.backend.backend.entity.patient;
import com.backend.backend.entity.Base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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
    @JoinColumn(name = "consultation_id", insertable = false, updatable = false)
    private Consultation consultation;

    @Column(name = "diagnosis", nullable = false, columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "diagnosis_code", length = 20)
    private String diagnosisCode;

    @Column(name = "severity", length = 20)
    private String severity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // createdAt and modifiedAt (updatedAt) inherited from AuditableEntity
}
