package com.backend.backend.repository.prescription;

import com.backend.backend.entity.perscription.Medicament;
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
 * Repository for Medicament entity.
 * Medicaments are typically read-only for doctors and managed by admins.
 */
@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, UUID>,
        JpaSpecificationExecutor<Medicament> {

    // ==================== BASIC QUERIES ====================

    /**
     * Find medicament by ID.
     */
    Optional<Medicament> findByMedicamentId(UUID medicamentId);

    /**
     * Find medicament by exact name.
     */
    Optional<Medicament> findByNameIgnoreCase(String name);

    /**
     * Check if medicament exists by name.
     */
    boolean existsByNameIgnoreCase(String name);

    // ==================== SEARCH ====================

    /**
     * Search medicaments by name (partial match).
     */
    @Query("SELECT m FROM Medicament m " +
           "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND m.isActive = true " +
           "ORDER BY m.name")
    List<Medicament> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Search medicaments by name - paginated.
     */
    @Query("SELECT m FROM Medicament m " +
           "WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND m.isActive = true")
    Page<Medicament> searchByNamePaged(
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    /**
     * Search with multiple filters.
     */
    @Query("SELECT m FROM Medicament m " +
           "WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:category IS NULL OR LOWER(m.category) = LOWER(:category)) " +
           "AND (:form IS NULL OR LOWER(m.form) = LOWER(:form)) " +
           "AND m.isActive = true")
    Page<Medicament> searchWithFilters(
            @Param("name") String name,
            @Param("category") String category,
            @Param("form") String form,
            Pageable pageable
    );

    // ==================== BY ACTIVE STATUS ====================

    /**
     * Find all active medicaments.
     */
    List<Medicament> findByIsActiveTrue();

    /**
     * Find all active medicaments - paginated.
     */
    Page<Medicament> findByIsActiveTrue(Pageable pageable);

    /**
     * Find all inactive medicaments - paginated.
     */
    Page<Medicament> findByIsActiveFalse(Pageable pageable);

    // ==================== BY CATEGORY ====================

    /**
     * Find medicaments by category.
     */
    @Query("SELECT m FROM Medicament m " +
           "WHERE LOWER(m.category) = LOWER(:category) " +
           "AND m.isActive = true " +
           "ORDER BY m.name")
    List<Medicament> findByCategory(@Param("category") String category);

    /**
     * Find medicaments by category - paginated.
     */
    @Query("SELECT m FROM Medicament m " +
           "WHERE LOWER(m.category) = LOWER(:category) " +
           "AND m.isActive = true")
    Page<Medicament> findByCategoryPaged(
            @Param("category") String category,
            Pageable pageable
    );

    /**
     * Get all distinct categories.
     */
    @Query("SELECT DISTINCT m.category FROM Medicament m " +
           "WHERE m.category IS NOT NULL " +
           "AND m.isActive = true " +
           "ORDER BY m.category")
    List<String> findAllCategories();

    // ==================== BY FORM ====================

    /**
     * Find medicaments by form (tablet, capsule, syrup, etc.).
     */
    @Query("SELECT m FROM Medicament m " +
           "WHERE LOWER(m.form) = LOWER(:form) " +
           "AND m.isActive = true " +
           "ORDER BY m.name")
    List<Medicament> findByForm(@Param("form") String form);

    /**
     * Get all distinct forms.
     */
    @Query("SELECT DISTINCT m.form FROM Medicament m " +
           "WHERE m.form IS NOT NULL " +
           "AND m.isActive = true " +
           "ORDER BY m.form")
    List<String> findAllForms();

    // ==================== STATISTICS ====================

    /**
     * Count active medicaments.
     */
    long countByIsActiveTrue();

    /**
     * Count medicaments by category.
     */
    @Query("SELECT COUNT(m) FROM Medicament m " +
           "WHERE LOWER(m.category) = LOWER(:category) " +
           "AND m.isActive = true")
    long countByCategory(@Param("category") String category);

    // ==================== MOST PRESCRIBED ====================

    /**
     * Get most prescribed medicaments.
     * Joins with PrescriptionItem to count usage.
     */
    @Query("SELECT m, COUNT(pi) as usageCount FROM Medicament m " +
           "LEFT JOIN PrescriptionItem pi ON pi.medicament.medicamentId = m.medicamentId " +
           "WHERE m.isActive = true " +
           "GROUP BY m " +
           "ORDER BY usageCount DESC")
    List<Object[]> findMostPrescribed(Pageable pageable);

    /**
     * Get most prescribed medicaments by a specific doctor.
     */
    @Query("SELECT m, COUNT(pi) as usageCount FROM Medicament m " +
           "LEFT JOIN PrescriptionItem pi ON pi.medicament.medicamentId = m.medicamentId " +
           "LEFT JOIN pi.prescription p " +
           "WHERE m.isActive = true " +
           "AND p.consultation.doctor.userId = :doctorId " +
           "GROUP BY m " +
           "ORDER BY usageCount DESC")
    List<Object[]> findMostPrescribedByDoctor(
            @Param("doctorId") UUID doctorId,
            Pageable pageable
    );
}

