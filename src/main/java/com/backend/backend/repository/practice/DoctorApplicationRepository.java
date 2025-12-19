package com.backend.backend.repository.practice;

import com.backend.backend.config.ApplicationConfig;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.practice.DoctorApplication;
import com.backend.backend.enums.ApplicationStatus;
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

    DoctorApplication findDoctorApplicationByEmail(String email);
    DoctorApplication findDoctorApplicationByCin(String CIN);
    DoctorApplication findDoctorApplicationByUsername(String username);

    boolean existsByCinAndStatus(String cin, String status);
    boolean existsByEmailAndStatus(String email, String status);

    boolean existsByUsernameAndStatus(String username, String status);

    boolean existsByLicenseNumberAndStatus(String licenseNumber, String status);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM DoctorApplication a " +
           "WHERE a.email = :email AND a.status = com.backend.backend.enums.ApplicationStatus.PENDING " +
           "OR EXISTS (SELECT 1 FROM User u WHERE u.email = :email)")
    boolean isEmailTaken(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM DoctorApplication a " +
           "WHERE a.username = :username AND a.status = com.backend.backend.enums.ApplicationStatus.PENDING " +
           "OR EXISTS (SELECT 1 FROM User u WHERE u.username = :username)")
    boolean isUsernameTaken(@Param("username") String username);
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM DoctorApplication a " +
           "WHERE a.licenseNumber = :licenseNumber AND a.status = com.backend.backend.enums.ApplicationStatus.PENDING " +
           "OR EXISTS (SELECT 1 FROM Doctor d WHERE d.licenseNumber = :licenseNumber)")
    boolean isLicenseNumberTaken(@Param("licenseNumber") String licenseNumber);
    
    Optional<DoctorApplication> findByEmail(String email);

    Optional<DoctorApplication> findByEmailAndStatus(String email, String status);

    @Query("SELECT a FROM DoctorApplication a WHERE a.status = com.backend.backend.enums.ApplicationStatus.PENDING " +
            "ORDER BY a.applicationDate ASC")
    List<DoctorApplication> findPendingApplications();
    List<DoctorApplication> findByStatusOrderByApplicationDateDesc(ApplicationStatus status);
    @Transactional
    @Modifying
    @Query("UPDATE DoctorApplication a " +
            "SET a.status = :status, " +
            "    a.processedDate = :processedDate, " +
            "    a.processedByAdmin.userId = :adminId, " +
            "    a.rejectionReason = :rejectionReason " +
            "WHERE a.applicationId = :applicationId AND a.status = com.backend.backend.enums.ApplicationStatus.PENDING")
    int processApplication(
            @Param("applicationId") UUID applicationId,
            @Param("status") ApplicationStatus status,
            @Param("processedDate") LocalDate processedDate,
            @Param("adminId") UUID adminId,
            @Param("rejectionReason") String rejectionReason
    );
    Optional<DoctorApplication> findByApplicationIdAndStatus(UUID applicationId, ApplicationStatus status);
}
