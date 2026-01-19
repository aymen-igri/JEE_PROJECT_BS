package com.backend.backend.repository.prescription;

import com.backend.backend.entity.perscription.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PrescriptionItem entity.
 * Items are accessed through their parent Prescription.
 */
@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, UUID> {

    // ==================== BASIC QUERIES ====================

    /**
     * Find item by ID with prescription and medicament fetched.
     */
    @Query("SELECT pi FROM PrescriptionItem pi " +
           "JOIN FETCH pi.prescription p " +
           "JOIN FETCH pi.medicament " +
           "WHERE pi.itemId = :id")
    Optional<PrescriptionItem> findByIdWithDetails(@Param("id") UUID id);

    /**
     * Find item by ID - ownership validated through prescription chain.
     * SECURITY: Ensures doctor can only access items from their prescriptions.
     */
    @Query("SELECT pi FROM PrescriptionItem pi " +
           "JOIN FETCH pi.prescription p " +
           "JOIN FETCH pi.medicament " +
           "WHERE pi.itemId = :id " +
           "AND p.consultation.doctor.userId = :doctorId")
    Optional<PrescriptionItem> findByIdAndDoctorId(
            @Param("id") UUID id,
            @Param("doctorId") UUID doctorId
    );

    // ==================== OWNERSHIP VALIDATION ====================

    /**
     * Check if item exists and belongs to doctor's prescription.
     * SECURITY: Use before update/delete operations.
     */
    @Query("SELECT CASE WHEN COUNT(pi) > 0 THEN true ELSE false END " +
           "FROM PrescriptionItem pi " +
           "WHERE pi.itemId = :itemId " +
           "AND pi.prescription.consultation.doctor.userId = :doctorId")
    boolean existsByIdAndDoctorId(
            @Param("itemId") UUID itemId,
            @Param("doctorId") UUID doctorId
    );

    // ==================== BY PRESCRIPTION ====================

    /**
     * Find all items for a prescription.
     */
    List<PrescriptionItem> findByPrescriptionPrescriptionId(UUID prescriptionId);

    /**
     * Find all items for a prescription with medicament details.
     */
    @Query("SELECT pi FROM PrescriptionItem pi " +
           "JOIN FETCH pi.medicament " +
           "WHERE pi.prescription.prescriptionId = :prescriptionId " +
           "ORDER BY pi.medicament.name")
    List<PrescriptionItem> findByPrescriptionIdWithMedicament(
            @Param("prescriptionId") UUID prescriptionId
    );

    /**
     * Find items for prescription with security check.
     * SECURITY: Only returns if prescription belongs to doctor.
     */
    @Query("SELECT pi FROM PrescriptionItem pi " +
           "JOIN FETCH pi.medicament " +
           "WHERE pi.prescription.prescriptionId = :prescriptionId " +
           "AND pi.prescription.consultation.doctor.userId = :doctorId")
    List<PrescriptionItem> findByPrescriptionIdAndDoctorId(
            @Param("prescriptionId") UUID prescriptionId,
            @Param("doctorId") UUID doctorId
    );

    // ==================== BY MEDICAMENT ====================

    /**
     * Find all items using a specific medicament.
     */
    List<PrescriptionItem> findByMedicamentMedicamentId(UUID medicamentId);

    /**
     * Count how many times a medicament has been prescribed.
     */
    long countByMedicamentMedicamentId(UUID medicamentId);

    /**
     * Find items by medicament for a specific doctor.
     */
    @Query("SELECT pi FROM PrescriptionItem pi " +
           "JOIN FETCH pi.prescription p " +
           "WHERE pi.medicament.medicamentId = :medicamentId " +
           "AND p.consultation.doctor.userId = :doctorId " +
           "ORDER BY p.prescribedDate DESC")
    List<PrescriptionItem> findByMedicamentIdAndDoctorId(
            @Param("medicamentId") UUID medicamentId,
            @Param("doctorId") UUID doctorId
    );

    // ==================== STATISTICS ====================

    /**
     * Count items in a prescription.
     */
    long countByPrescriptionPrescriptionId(UUID prescriptionId);

    /**
     * Sum total quantity of a medicament prescribed by doctor.
     */
    @Query("SELECT COALESCE(SUM(pi.quantity), 0) FROM PrescriptionItem pi " +
           "WHERE pi.medicament.medicamentId = :medicamentId " +
           "AND pi.prescription.consultation.doctor.userId = :doctorId")
    long sumQuantityByMedicamentIdAndDoctorId(
            @Param("medicamentId") UUID medicamentId,
            @Param("doctorId") UUID doctorId
    );
}

