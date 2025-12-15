package com.backend.backend.repository.practice;

import com.backend.backend.entity.practice.DoctorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorApplicationRepository extends JpaRepository<DoctorApplication, UUID> {


    boolean existsByCinAndStatus(String cin, String status);
    boolean existsByEmailAndStatus(String email, String status);

    boolean existsByUsernameAndStatus(String username, String status);

    boolean existsByLicenseNumberAndStatus(String licenseNumber, String status);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM DoctorApplication a " +
           "WHERE a.email = :email AND a.status = 'PENDING' " +
           "OR EXISTS (SELECT 1 FROM User u WHERE u.email = :email)")
    boolean isEmailTaken(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM DoctorApplication a " +
           "WHERE a.username = :username AND a.status = 'PENDING' " +
           "OR EXISTS (SELECT 1 FROM User u WHERE u.username = :username)")
    boolean isUsernameTaken(@Param("username") String username);
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM DoctorApplication a " +
           "WHERE a.licenseNumber = :licenseNumber AND a.status = 'PENDING' " +
           "OR EXISTS (SELECT 1 FROM Doctor d WHERE d.licenseNumber = :licenseNumber)")
    boolean isLicenseNumberTaken(@Param("licenseNumber") String licenseNumber);
    
    Optional<DoctorApplication> findByEmail(String email);

    Optional<DoctorApplication> findByEmailAndStatus(String email, String status);

    @Query("SELECT a FROM DoctorApplication a WHERE a.status = 'PENDING' " +
            "ORDER BY a.applicationDate ASC")
    List<DoctorApplication> findPendingApplications();
    List<DoctorApplication> findByStatusOrderByApplicationDateDesc(String status);
    @Transactional
    @Modifying
    @Query("UPDATE DoctorApplication a " +
            "SET a.status = :status, " +
            "    a.processedDate = :processedDate, " +
            "    a.processedByAdmin.userId = :adminId, " +
            "    a.rejectionReason = :rejectionReason " +
            "WHERE a.applicationId = :applicationId AND a.status = 'PENDING'")
    int processApplication(
            @Param("applicationId") UUID applicationId,
            @Param("status") String status,
            @Param("processedDate") LocalDate processedDate,
            @Param("adminId") UUID adminId,
            @Param("rejectionReason") String rejectionReason
    );
    Optional<DoctorApplication> findByApplicationIdAndStatus(UUID applicationId, String status);
}
