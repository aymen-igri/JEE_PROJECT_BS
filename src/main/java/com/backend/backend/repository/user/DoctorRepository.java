
// ============================================
// DOCTOR REPOSITORY
// ============================================


package com.backend.backend.repository.user;

import com.backend.backend.entity.User.Doctor;
import com.backend.backend.enums.EStatus;
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

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID>,
        JpaSpecificationExecutor<Doctor> {


    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    Page<Doctor> findBySpecialty(String specialty, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.specialty = :specialty AND d.status = com.backend.backend.enums.EStatus.ACTIVE " +
            "ORDER BY d.fullName")
    List<Doctor> findActiveBySpecialty(@Param("specialty") String specialty);

    @Query("SELECT d FROM Doctor d WHERE d.specialty = :specialty AND d.status = com.backend.backend.enums.EStatus.ACTIVE")
    Page<Doctor> findActiveBySpecialtyPaged(@Param("specialty") String specialty, Pageable pageable);

    @Query("SELECT DISTINCT d.specialty FROM Doctor d WHERE d.specialty IS NOT NULL ORDER BY d.specialty")
    List<String> findAllSpecialties();

    @Query("SELECT d FROM Doctor d WHERE d.registeredByAdmin.userId = :registeredByAdminId")
    List<Doctor> findByRegisteredByAdminId(@Param("registeredByAdminId") UUID registeredByAdminId);

    @Query("SELECT d FROM Doctor d WHERE d.registeredByAdmin.userId = :registeredByAdminId")
    Page<Doctor> findByRegisteredByAdminIdPaged(@Param("registeredByAdminId") UUID registeredByAdminId, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.status = com.backend.backend.enums.EStatus.ACTIVE ORDER BY d.fullName")
    List<Doctor> findAllActive();

    @Query("SELECT d FROM Doctor d WHERE d.status = com.backend.backend.enums.EStatus.ACTIVE")
    Page<Doctor> findAllActivePaged(Pageable pageable);

    Page<Doctor> findByStatus(EStatus status, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE " +
            "LOWER(d.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.specialty) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(d.licenseNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Doctor> searchDoctors(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE " +
            "(:specialty IS NULL OR d.specialty = :specialty) AND " +
            "(:status IS NULL OR d.status = :status) AND " +
            "(:searchTerm IS NULL OR LOWER(d.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Doctor> searchWithFilters(
            @Param("specialty") String specialty,
            @Param("status") EStatus status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );

    @Query("SELECT DISTINCT d FROM Doctor d " +
            "WHERE d.status = com.backend.backend.enums.EStatus.ACTIVE " +
            "AND SIZE(d.cabinets) > 0")
    List<Doctor> findDoctorsWithCabinets();

    @Query("SELECT DISTINCT d FROM Doctor d " +
            "LEFT JOIN FETCH d.cabinets " +
            "WHERE d.status = com.backend.backend.enums.EStatus.ACTIVE " +
            "AND SIZE(d.cabinets) > 0")
    List<Doctor> findDoctorsWithCabinetsFetched();

    @Query("SELECT d FROM Doctor d " +
            "LEFT JOIN FETCH d.cabinets " +
            "WHERE d.userId = :doctorId")
    Optional<Doctor> findByIdWithCabinets(@Param("doctorId") UUID doctorId);

    @Query("SELECT d FROM Doctor d " +
            "LEFT JOIN FETCH d.registeredByAdmin " +
            "WHERE d.userId = :doctorId")
    Optional<Doctor> findByIdWithAdmin(@Param("doctorId") UUID doctorId);
    @Query("SELECT d.userId, d.fullName, SIZE(d.cabinets) FROM Doctor d " +
            "GROUP BY d.userId, d.fullName")
    List<Object[]> countCabinetsPerDoctor();
    @Query("SELECT DISTINCT d FROM Doctor d " +
            "INNER JOIN Consultation c ON c.doctor.userId = d.userId " +
            "WHERE c.consultationDate BETWEEN :startDate AND :endDate")
    List<Doctor> findDoctorsWithConsultationsInPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query("SELECT d.userId, d.fullName, COUNT(c) as consultationCount " +
            "FROM Doctor d " +
            "LEFT JOIN Consultation c ON c.doctor.userId = d.userId " +
            "AND c.consultationDate BETWEEN :startDate AND :endDate " +
            "GROUP BY d.userId, d.fullName " +
            "ORDER BY consultationCount DESC")
    List<Object[]> countConsultationsPerDoctorInPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
    @Query("SELECT d FROM Doctor d WHERE d.status = com.backend.backend.enums.EStatus.ACTIVE " +
            "AND d.userId NOT IN (" +
            "  SELECT a.doctor.userId FROM Appointment a " +
            "  WHERE DATE(a.appointmentDateTime) = :date " +
            "  AND a.status NOT IN ('CANCELLED', 'NO_SHOW')" +
            ")")
    List<Doctor> findAvailableDoctors(@Param("date") LocalDate date);

    @Query("SELECT d FROM Doctor d WHERE d.status = com.backend.backend.enums.EStatus.ACTIVE " +
            "AND d.userId NOT IN (" +
            "  SELECT a.doctor.userId FROM Appointment a " +
            "  WHERE DATE(a.appointmentDateTime) = :date " +
            "  AND a.status NOT IN ('CANCELLED', 'NO_SHOW')" +
            ")")
    Page<Doctor> findAvailableDoctorsPaged(@Param("date") LocalDate date, Pageable pageable);
}

