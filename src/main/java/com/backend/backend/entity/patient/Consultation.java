package com.backend.backend.entity.patient;

import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.enums.ConsultationStatus;
import com.backend.backend.enums.ConsultationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.List;
import java.util.UUID;

/**
 * Consultation entity - represents an ongoing medical consultation for a patient.
 * A consultation can span multiple appointments and has no specific date.
 * It is tracked only by its status (IN_PROGRESS, COMPLETED, CANCELLED).
 * Diagnostics and Prescriptions are linked to consultations with their own dates.
 */
@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Consultation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "consultation_id")
    private UUID consultationId;

    /**
     * The patient this consultation belongs to.
     * Consultations are created directly via patient ID.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * The doctor managing this consultation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type", length = 30)
    private ConsultationType consultationType;

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "vital_signs", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> vitalSigns;

    @Column(name = "physical_exam", columnDefinition = "TEXT")
    private String physicalExam;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * The status of the consultation.
     * Tracked only by status - no date field.
     * Valid transitions: IN_PROGRESS -> COMPLETED, IN_PROGRESS -> CANCELLED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ConsultationStatus status = ConsultationStatus.IN_PROGRESS;

    // createdAt and updatedAt inherited from AuditableEntity
}