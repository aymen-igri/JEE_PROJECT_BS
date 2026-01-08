package com.backend.backend.repository.prescription;

import com.backend.backend.entity.perscription.Analysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Analysis entity (medical examinations: lab tests, radiology, etc.).
 * Note: Analysis entity uses Integer ID - consider migrating to UUID for consistency.
 */
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Integer> {

    // ==================== BASIC QUERIES ====================

    /**
     * Find analysis by ID with prescription fetched.
     */
    @Query("SELECT a FROM Analysis a " +
           "JOIN FETCH a.prescription p " +
           "JOIN FETCH p.consultation c " +
           "JOIN FETCH c.doctor " +
           "WHERE a.analysisId = :id")
    Optional<Analysis> findByIdWithDetails(@Param("id") Integer id);

    /**
     * Find analysis by ID - ownership validated.
     * SECURITY: Ensures doctor can only access analyses from their prescriptions.
     */
    @Query("SELECT a FROM Analysis a " +
           "JOIN FETCH a.prescription p " +
           "WHERE a.analysisId = :id " +
           "AND p.consultation.doctor.userId = :doctorId")
    Optional<Analysis> findByIdAndDoctorId(
            @Param("id") Integer id,
            @Param("doctorId") UUID doctorId
    );

    // ==================== OWNERSHIP VALIDATION ====================

    /**
     * Check if analysis exists and belongs to doctor's prescription.
     * SECURITY: Use before update/delete operations.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Analysis a " +
           "WHERE a.analysisId = :analysisId " +
           "AND a.prescription.consultation.doctor.userId = :doctorId")
    boolean existsByIdAndDoctorId(
            @Param("analysisId") Integer analysisId,
            @Param("doctorId") UUID doctorId
    );

    // ==================== BY PRESCRIPTION ====================

    /**
     * Find all analyses for a prescription.
     */
    List<Analysis> findByPrescriptionPrescriptionId(UUID prescriptionId);

    /**
     * Find analyses for prescription with security check.
     * SECURITY: Only returns if prescription belongs to doctor.
     */
    @Query("SELECT a FROM Analysis a " +
           "WHERE a.prescription.prescriptionId = :prescriptionId " +
           "AND a.prescription.consultation.doctor.userId = :doctorId " +
           "ORDER BY a.analysisName")
    List<Analysis> findByPrescriptionIdAndDoctorId(
            @Param("prescriptionId") UUID prescriptionId,
            @Param("doctorId") UUID doctorId
    );

    // ==================== BY TYPE ====================

    /**
     * Find analyses by type.
     */
    @Query("SELECT a FROM Analysis a " +
           "WHERE LOWER(a.analysisType) = LOWER(:type) " +
           "ORDER BY a.analysisName")
    List<Analysis> findByType(@Param("type") String type);

    /**
     * Find analyses by type for a doctor.
     */
    @Query("SELECT a FROM Analysis a " +
           "WHERE LOWER(a.analysisType) = LOWER(:type) " +
           "AND a.prescription.consultation.doctor.userId = :doctorId " +
           "ORDER BY a.analysisName")
    List<Analysis> findByTypeAndDoctorId(
            @Param("type") String type,
            @Param("doctorId") UUID doctorId
    );

    /**
     * Get all distinct analysis types.
     */
    @Query("SELECT DISTINCT a.analysisType FROM Analysis a " +
           "WHERE a.analysisType IS NOT NULL " +
           "ORDER BY a.analysisType")
    List<String> findAllTypes();

    // ==================== BY PATIENT ====================

    /**
     * Find all analyses for a patient.
     * Updated: Consultation now has direct patient link.
     */
    @Query("SELECT a FROM Analysis a " +
           "JOIN a.prescription p " +
           "JOIN p.consultation c " +
           "WHERE c.patient.patientId = :patientId " +
           "ORDER BY p.prescribedDate DESC")
    List<Analysis> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find analyses for patient - paginated.
     * Updated: Consultation now has direct patient link.
     */
    @Query("SELECT a FROM Analysis a " +
           "WHERE a.prescription.consultation.patient.patientId = :patientId")
    Page<Analysis> findByPatientIdPaged(
            @Param("patientId") UUID patientId,
            Pageable pageable
    );

    // ==================== BY RESULTS ====================

    /**
     * Find analyses with results.
     */
    @Query("SELECT a FROM Analysis a " +
           "WHERE a.results IS NOT NULL " +
           "AND a.prescription.consultation.doctor.userId = :doctorId " +
           "ORDER BY a.analysisId DESC")
    List<Analysis> findWithResultsByDoctorId(@Param("doctorId") UUID doctorId);

    /**
     * Find analyses without results (pending).
     */
    @Query("SELECT a FROM Analysis a " +
           "WHERE a.results IS NULL " +
           "AND a.prescription.consultation.doctor.userId = :doctorId " +
           "ORDER BY a.analysisId DESC")
    List<Analysis> findPendingByDoctorId(@Param("doctorId") UUID doctorId);


    // ==================== STATISTICS ====================

    /**
     * Count analyses by prescription.
     */
    long countByPrescriptionPrescriptionId(UUID prescriptionId);

    /**
     * Count analyses by type.
     */
    @Query("SELECT COUNT(a) FROM Analysis a " +
           "WHERE LOWER(a.analysisType) = LOWER(:type)")
    long countByType(@Param("type") String type);

    /**
     * Count pending analyses (without results) for a doctor.
     */
    @Query("SELECT COUNT(a) FROM Analysis a " +
           "WHERE a.results IS NULL " +
           "AND a.prescription.consultation.doctor.userId = :doctorId")
    long countPendingByDoctorId(@Param("doctorId") UUID doctorId);
}

