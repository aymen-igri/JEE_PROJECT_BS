package com.backend.backend.repository.prescription;

import com.backend.backend.entity.perscription.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Prescription entity.
 * Includes security-focused queries for ownership validation.
 * Updated: Consultation now has direct patient link instead of via record.
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID>,
        JpaSpecificationExecutor<Prescription> {

    // ==================== BASIC QUERIES ====================

    /**
     * Find prescription by ID with consultation fetched.
     */
    @Query("SELECT p FROM Prescription p " +
           "JOIN FETCH p.consultation c " +
           "JOIN FETCH c.doctor " +
           "JOIN FETCH c.patient " +
           "WHERE p.prescriptionId = :id")
    Optional<Prescription> findByIdWithDetails(@Param("id") UUID id);

    /**
     * Find prescription by ID - ownership validated.
     * SECURITY: Ensures doctor can only access prescriptions from their consultations.
     */
    @Query("SELECT p FROM Prescription p " +
           "JOIN FETCH p.consultation c " +
           "WHERE p.prescriptionId = :id " +
           "AND c.doctor.userId = :doctorId")
    Optional<Prescription> findByIdAndDoctorId(
            @Param("id") UUID id,
            @Param("doctorId") UUID doctorId
    );

    // ==================== OWNERSHIP VALIDATION ====================

    /**
     * Check if prescription exists and belongs to doctor's consultation.
     * SECURITY: Use before update/delete operations.
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Prescription p " +
           "WHERE p.prescriptionId = :prescriptionId " +
           "AND p.consultation.doctor.userId = :doctorId")
    boolean existsByIdAndDoctorId(
            @Param("prescriptionId") UUID prescriptionId,
            @Param("doctorId") UUID doctorId
    );

    /**
     * Check if prescription exists for a consultation.
     */
    boolean existsByConsultationConsultationId(UUID consultationId);

    // ==================== BY CONSULTATION ====================

    /**
     * Find all prescriptions for a consultation.
     */
    List<Prescription> findByConsultationConsultationId(UUID consultationId);

    /**
     * Find all prescriptions for a consultation - paginated.
     */
    Page<Prescription> findByConsultationConsultationId(UUID consultationId, Pageable pageable);

    /**
     * Find prescriptions for consultation with security check.
     * SECURITY: Only returns if consultation belongs to doctor.
     */
    @Query("SELECT p FROM Prescription p " +
           "WHERE p.consultation.consultationId = :consultationId " +
           "AND p.consultation.doctor.userId = :doctorId " +
           "ORDER BY p.prescribedDate DESC")
    List<Prescription> findByConsultationIdAndDoctorId(
            @Param("consultationId") UUID consultationId,
            @Param("doctorId") UUID doctorId
    );

    // ==================== BY DOCTOR ====================

    /**
     * Find all prescriptions by a doctor - paginated.
     */
    @Query("SELECT p FROM Prescription p " +
           "JOIN FETCH p.consultation c " +
           "WHERE c.doctor.userId = :doctorId " +
           "ORDER BY p.prescribedDate DESC")
    Page<Prescription> findByDoctorIdPaged(
            @Param("doctorId") UUID doctorId,
            Pageable pageable
    );

    /**
     * Find prescriptions by doctor within date range.
     */
    @Query("SELECT p FROM Prescription p " +
           "JOIN FETCH p.consultation c " +
           "WHERE c.doctor.userId = :doctorId " +
           "AND p.prescribedDate BETWEEN :startDate AND :endDate " +
           "ORDER BY p.prescribedDate DESC")
    List<Prescription> findByDoctorIdAndDateRange(
            @Param("doctorId") UUID doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ==================== BY PATIENT ====================

    /**
     * Find all prescriptions for a patient.
     * Updated: Consultation now has direct patient link.
     */
    @Query("SELECT p FROM Prescription p " +
           "JOIN FETCH p.consultation c " +
           "JOIN FETCH c.doctor " +
           "WHERE c.patient.patientId = :patientId " +
           "ORDER BY p.prescribedDate DESC")
    List<Prescription> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find prescriptions for patient - paginated.
     * Updated: Consultation now has direct patient link.
     */
    @Query("SELECT p FROM Prescription p " +
           "WHERE p.consultation.patient.patientId = :patientId")
    Page<Prescription> findByPatientIdPaged(
            @Param("patientId") UUID patientId,
            Pageable pageable
    );

    // ==================== STATISTICS ====================

    /**
     * Count prescriptions by consultation.
     */
    long countByConsultationConsultationId(UUID consultationId);

    /**
     * Count prescriptions by doctor.
     */
    @Query("SELECT COUNT(p) FROM Prescription p " +
           "WHERE p.consultation.doctor.userId = :doctorId")
    long countByDoctorId(@Param("doctorId") UUID doctorId);

    /**
     * Count prescriptions by doctor in date range.
     */
    @Query("SELECT COUNT(p) FROM Prescription p " +
           "WHERE p.consultation.doctor.userId = :doctorId " +
           "AND p.prescribedDate BETWEEN :startDate AND :endDate")
    long countByDoctorIdAndDateRange(
            @Param("doctorId") UUID doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

