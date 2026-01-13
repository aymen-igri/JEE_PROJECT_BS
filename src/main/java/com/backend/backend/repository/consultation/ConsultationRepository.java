package com.backend.backend.repository.consultation;

import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.enums.ConsultationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Consultation entity.
 * Consultations are created via patient ID and have no date.
 * Includes security-focused queries for ownership validation.
 */
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID>,
        JpaSpecificationExecutor<Consultation> {

    // ==================== BASIC QUERIES ====================

    /**
     * Find consultation by ID with all related entities fetched.
     * Use this to avoid N+1 queries.
     */
    @Query("SELECT c FROM Consultation c " +
           "JOIN FETCH c.patient " +
           "JOIN FETCH c.doctor " +
           "WHERE c.consultationId = :id")
    Optional<Consultation> findByIdWithDetails(@Param("id") UUID id);

    /**
     * Find consultation by ID - ownership validated.
     * SECURITY: Ensures doctor can only access their own consultations.
     */
        
    @Query("SELECT c FROM Consultation c " +
           "JOIN FETCH c.patient " +
           "WHERE c.consultationId = :id AND c.doctor.userId = :doctorId")
    Optional<Consultation> findByIdAndDoctorId(
            @Param("id") UUID id,
            @Param("doctorId") UUID doctorId
    );
        
    @Query("SELECT c FROM Consultation c " +
            "LEFT JOIN FETCH c.patient p " +
            "LEFT JOIN FETCH c.doctor " +
            "WHERE c.doctor.userId = :doctorId " +
            "ORDER BY c.updatedAt DESC")
    List<Consultation> findLatestConsultationsWithDetails(@Param("doctorId") UUID doctorId);

    // ==================== OWNERSHIP VALIDATION ====================

    /**
     * Check if consultation exists and belongs to doctor.
     * SECURITY: Use before update/delete operations.
     */
    boolean existsByConsultationIdAndDoctorUserId(UUID consultationId, UUID doctorId);

    // ==================== BY DOCTOR ====================

    /**
     * Find all consultations by doctor - paginated.
     */
    @Query("SELECT c FROM Consultation c " +
           "JOIN FETCH c.patient " +
           "WHERE c.doctor.userId = :doctorId")
    Page<Consultation> findByDoctorIdPaged(
            @Param("doctorId") UUID doctorId,
            Pageable pageable
    );

    // ==================== BY PATIENT ====================

    /**
     * Find all consultations for a patient.
     */
    @Query("SELECT c FROM Consultation c " +
           "JOIN FETCH c.doctor " +
           "WHERE c.patient.patientId = :patientId " +
           "ORDER BY c.createdAt DESC")
    List<Consultation> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find consultations for patient - paginated.
     */
    @Query("SELECT c FROM Consultation c " +
           "WHERE c.patient.patientId = :patientId")
    Page<Consultation> findByPatientIdPaged(
            @Param("patientId") UUID patientId,
            Pageable pageable
    );

    // ==================== BY STATUS ====================

    /**
     * Find consultations by status.
     */
    @Query("SELECT c FROM Consultation c " +
           "WHERE c.doctor.userId = :doctorId " +
           "AND c.status = :status")
    Page<Consultation> findByDoctorIdAndStatusPaged(
            @Param("doctorId") UUID doctorId,
            @Param("status") ConsultationStatus status,
            Pageable pageable
    );

    // ==================== STATISTICS ====================

    /**
     * Count consultations by doctor.
     */
    long countByDoctorUserId(UUID doctorId);

    /**
     * Count consultations by status for a doctor.
     */
    long countByDoctorUserIdAndStatus(UUID doctorId, ConsultationStatus status);

    /**
     * Count consultations by patient.
     */
    long countByPatientPatientId(UUID patientId);
}

