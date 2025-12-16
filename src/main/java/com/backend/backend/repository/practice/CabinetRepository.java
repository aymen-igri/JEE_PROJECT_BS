package com.backend.backend.repository.practice;

import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.entity.practice.DoctorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CabinetRepository extends JpaRepository<Cabinet, UUID> {

    boolean existsByDoctorUserIdAndStatus(UUID doctorId, String status);

    @Query("SELECT c.cabinetId as cabinetId, c.name as name, c.status as status " +
            "FROM Cabinet c WHERE c.doctor.userId = :doctorId")
    List<CabinetSummaryProjection> findCabinetsByDoctorId(@Param("doctorId") UUID doctorId);

    Optional<Cabinet> findByCabinetId(UUID cabinetId);

    @Modifying
    @Query("UPDATE Cabinet c SET c.status = :status WHERE c.cabinetId = :cabinetId")
    int updateStatus(@Param("cabinetId") UUID cabinetId, @Param("status") String status);

    Optional<Cabinet> findByCabinetIdAndStatus(UUID cabinetId, String status);

    @Query("SELECT c FROM Cabinet c JOIN FETCH c.doctor WHERE c.cabinetId = :cabinetId")
    Optional<Cabinet> findByIdWithOwner(@Param("cabinetId") UUID cabinetId);
}

interface CabinetSummaryProjection {
    UUID getCabinetId();
    String getName();
    String getStatus();
}
