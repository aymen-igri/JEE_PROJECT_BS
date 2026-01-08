package com.backend.backend.repository.consultation;

import com.backend.backend.entity.patient.Diagnostic;
import com.backend.backend.enums.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Diagnostic entity.
 * Includes security-focused queries for ownership validation.
 * Supports grace period checks via createdAt from AuditableEntity.
 * Updated: Consultation now has direct patient link instead of via record.
 */
@Repository
public interface DiagnosticRepository extends JpaRepository<Diagnostic, UUID>,
        JpaSpecificationExecutor<Diagnostic> {

    // ==================== BASIC QUERIES ====================

    /**
     * Find diagnostic by ID with consultation fetched.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "JOIN FETCH d.consultation c " +
           "JOIN FETCH c.doctor " +
           "JOIN FETCH c.patient " +
           "WHERE d.diagnosisId = :id")
    Optional<Diagnostic> findByIdWithDetails(@Param("id") UUID id);

    /**
     * Find diagnostic by ID - ownership validated.
     * SECURITY: Ensures doctor can only access diagnostics from their consultations.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "JOIN FETCH d.consultation c " +
           "WHERE d.diagnosisId = :id " +
           "AND c.doctor.userId = :doctorId")
    Optional<Diagnostic> findByIdAndDoctorId(
            @Param("id") UUID id,
            @Param("doctorId") UUID doctorId
    );

    // ==================== OWNERSHIP VALIDATION ====================

    /**
     * Check if diagnostic exists and belongs to doctor's consultation.
     * SECURITY: Use before update/delete operations.
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
           "FROM Diagnostic d " +
           "WHERE d.diagnosisId = :diagnosisId " +
           "AND d.consultation.doctor.userId = :doctorId")
    boolean existsByIdAndDoctorId(
            @Param("diagnosisId") UUID diagnosisId,
            @Param("doctorId") UUID doctorId
    );

    /**
     * Check if diagnostic exists for a consultation.
     */
    boolean existsByConsultationConsultationId(UUID consultationId);

    // ==================== GRACE PERIOD SUPPORT ====================

    /**
     * Find diagnostic with creation time for grace period validation.
     * Returns diagnostic only if it belongs to the doctor.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "JOIN FETCH d.consultation c " +
           "WHERE d.diagnosisId = :id " +
           "AND c.doctor.userId = :doctorId")
    Optional<Diagnostic> findByIdForGracePeriodCheck(
            @Param("id") UUID id,
            @Param("doctorId") UUID doctorId
    );

    /**
     * Check if diagnostic is within grace period.
     * Returns true if createdAt + gracePeriod > now.
     */
    @Query("SELECT CASE WHEN d.createdAt > :cutoffTime THEN true ELSE false END " +
           "FROM Diagnostic d " +
           "WHERE d.diagnosisId = :id")
    boolean isWithinGracePeriod(
            @Param("id") UUID id,
            @Param("cutoffTime") LocalDateTime cutoffTime
    );

    // ==================== BY CONSULTATION ====================

    /**
     * Find all diagnostics for a consultation.
     */
    List<Diagnostic> findByConsultationConsultationId(UUID consultationId);

    /**
     * Find all diagnostics for a consultation - paginated.
     */
    Page<Diagnostic> findByConsultationConsultationId(UUID consultationId, Pageable pageable);

    /**
     * Find diagnostics for consultation with security check.
     * SECURITY: Only returns if consultation belongs to doctor.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "WHERE d.consultation.consultationId = :consultationId " +
           "AND d.consultation.doctor.userId = :doctorId " +
           "ORDER BY d.createdAt DESC")
    List<Diagnostic> findByConsultationIdAndDoctorId(
            @Param("consultationId") UUID consultationId,
            @Param("doctorId") UUID doctorId
    );

    // ==================== BY DOCTOR ====================

    /**
     * Find all diagnostics by a doctor - paginated.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "JOIN FETCH d.consultation c " +
           "WHERE c.doctor.userId = :doctorId " +
           "ORDER BY d.createdAt DESC")
    Page<Diagnostic> findByDoctorIdPaged(
            @Param("doctorId") UUID doctorId,
            Pageable pageable
    );

    // ==================== BY PATIENT ====================

    /**
     * Find all diagnostics for a patient.
     * Updated: Consultation now has direct patient link.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "JOIN FETCH d.consultation c " +
           "JOIN FETCH c.doctor " +
           "WHERE c.patient.patientId = :patientId " +
           "ORDER BY d.createdAt DESC")
    List<Diagnostic> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find diagnostics for patient - paginated.
     * Updated: Consultation now has direct patient link.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "WHERE d.consultation.patient.patientId = :patientId")
    Page<Diagnostic> findByPatientIdPaged(
            @Param("patientId") UUID patientId,
            Pageable pageable
    );

    // ==================== BY SEVERITY ====================

    /**
     * Find diagnostics by severity for a doctor.
     */
    @Query("SELECT d FROM Diagnostic d " +
           "WHERE d.consultation.doctor.userId = :doctorId " +
           "AND d.severity = :severity " +
           "ORDER BY d.createdAt DESC")
    List<Diagnostic> findByDoctorIdAndSeverity(
            @Param("doctorId") UUID doctorId,
            @Param("severity") String severity
    );

    // ==================== STATISTICS ====================

    /**
     * Count diagnostics by consultation.
     */
    long countByConsultationConsultationId(UUID consultationId);

    /**
     * Count diagnostics by doctor.
     */
    @Query("SELECT COUNT(d) FROM Diagnostic d " +
           "WHERE d.consultation.doctor.userId = :doctorId")
    long countByDoctorId(@Param("doctorId") UUID doctorId);

    /**
     * Count diagnostics by severity for a doctor.
     */
    @Query("SELECT COUNT(d) FROM Diagnostic d " +
           "WHERE d.consultation.doctor.userId = :doctorId " +
           "AND d.severity = :severity")
    long countByDoctorIdAndSeverity(
            @Param("doctorId") UUID doctorId,
            @Param("severity") Severity severity
    );
}

