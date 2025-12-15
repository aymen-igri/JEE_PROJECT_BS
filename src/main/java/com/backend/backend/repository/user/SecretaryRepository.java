
package com.backend.backend.repository.user;

import com.backend.backend.entity.User.Secretary;
import com.backend.backend.enums.EStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SecretaryRepository extends JpaRepository<Secretary, UUID> {

    Optional<Secretary> findByUserId(UUID userId);

    @Query("SELECT s FROM Secretary s WHERE s.status = com.backend.backend.enums.EStatus.ACTIVE ORDER BY s.fullName")
    List<Secretary> findAllActive();

    Page<Secretary> findByStatus(EStatus status, Pageable pageable);

    @Query("SELECT s FROM Secretary s WHERE " +
            "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Secretary> searchSecretaries(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Secretary s " +
            "INNER JOIN CabinetMember cm ON cm.user.userId = s.userId " +
            "WHERE cm.cabinet.cabinetId = :cabinetId " +
            "AND cm.role = 'SECRETARY' " +
            "AND s.status = com.backend.backend.enums.EStatus.ACTIVE")
    List<Secretary> findByCabinetId(@Param("cabinetId") Integer cabinetId);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.registeredBySecretary.userId = :secretaryId")
    Long countPatientsRegisteredBy(@Param("secretaryId") UUID secretaryId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.scheduledBySecretary.userId = :secretaryId")
    Long countAppointmentsScheduledBy(@Param("secretaryId") UUID secretaryId);
}