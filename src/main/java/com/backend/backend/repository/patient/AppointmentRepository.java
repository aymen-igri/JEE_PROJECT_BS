package com.backend.backend.repository.patient;

import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {


    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.doctor " +
           "JOIN FETCH a.cabinet " +
           "LEFT JOIN FETCH a.scheduledBySecretary " +
           "WHERE a.appointmentId = :appointmentId")
    Optional<Appointment> findByAppointmentIdWithDetails(@Param("appointmentId") UUID appointmentId);

    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.cabinet " +
           "WHERE a.doctor.userId = :doctorId " +
           "AND a.status NOT IN :excludedStatuses " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findByDoctorUserIdWithDetails(
            @Param("doctorId") UUID doctorId,
            @Param("excludedStatuses") Collection<AppointmentStatus> excludedStatuses
    );

    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.doctor " +
           "JOIN FETCH a.cabinet " +
           "WHERE a.patient.patientId = :patientId " +
           "AND a.status NOT IN :excludedStatuses " +
           "ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findByPatientPatientIdWithDetails(
            @Param("patientId") UUID patientId,
            @Param("excludedStatuses") Collection<AppointmentStatus> excludedStatuses
    );

    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.doctor " +
           "JOIN FETCH a.cabinet " +
           "WHERE a.scheduledBySecretary.userId = :secretaryId " +
           "AND a.status NOT IN :excludedStatuses " +
           "ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findByScheduledBySecretaryUserIdWithDetails(
            @Param("secretaryId") UUID secretaryId,
            @Param("excludedStatuses") Collection<AppointmentStatus> excludedStatuses
    );

    // ==================== PAGINATED QUERIES ====================

    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.cabinet " +
           "WHERE a.doctor.userId = :doctorId " +
           "AND a.status NOT IN :excludedStatuses")
    Page<Appointment> findByDoctorUserIdWithDetailsPaged(
            @Param("doctorId") UUID doctorId,
            @Param("excludedStatuses") Collection<AppointmentStatus> excludedStatuses,
            Pageable pageable
    );

    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.doctor " +
           "JOIN FETCH a.cabinet " +
           "WHERE a.patient.patientId = :patientId " +
           "AND a.status NOT IN :excludedStatuses")
    Page<Appointment> findByPatientPatientIdWithDetailsPaged(
            @Param("patientId") UUID patientId,
            @Param("excludedStatuses") Collection<AppointmentStatus> excludedStatuses,
            Pageable pageable
    );

    // ==================== CONFLICT DETECTION ====================

    /**
     * Find appointments that overlap with the given time range for a specific doctor.
     * An overlap occurs when: existing_start < new_end AND existing_end > new_start
     */
    @Query(value = "SELECT a.* FROM appointments a " +
           "WHERE a.doctor_id = :doctorId " +
           "AND a.status NOT IN (:excludedStatuses) " +
           "AND a.appointment_date_time < :rangeEnd " +
           "AND (a.appointment_date_time + (COALESCE(a.duration, 30) * INTERVAL '1 minute')) > :rangeStart",
           nativeQuery = true)
    List<Appointment> findPotentialConflicts(
            @Param("doctorId") UUID doctorId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("excludedStatuses") List<String> excludedStatuses
    );

    /**
     * Find appointments that overlap with the given time range for a specific cabinet.
     * An overlap occurs when: existing_start < new_end AND existing_end > new_start
     */
    @Query(value = "SELECT a.* FROM appointments a " +
           "WHERE a.cabinet_id = :cabinetId " +
           "AND a.status NOT IN (:excludedStatuses) " +
           "AND a.appointment_date_time < :rangeEnd " +
           "AND (a.appointment_date_time + (COALESCE(a.duration, 30) * INTERVAL '1 minute')) > :rangeStart",
           nativeQuery = true)
    List<Appointment> findCabinetConflicts(
            @Param("cabinetId") UUID cabinetId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("excludedStatuses") List<String> excludedStatuses
    );

    // ==================== UTILITY METHODS ====================

    // Count methods
    long countByDoctor_UserIdAndStatus(UUID doctorId, AppointmentStatus status);

    long countByDoctor_UserIdAndStatusIn(UUID doctorId, Collection<AppointmentStatus> statuses);

    long countByCabinet_CabinetIdAndStatus(UUID cabinetId, AppointmentStatus status);

    // Upcoming appointments
    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.cabinet " +
           "WHERE a.doctor.userId = :doctorId " +
           "AND a.appointmentDateTime >= :fromDateTime " +
           "AND a.status IN :activeStatuses " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingByDoctorUserId(
            @Param("doctorId") UUID doctorId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("activeStatuses") Collection<AppointmentStatus> activeStatuses
    );

    // Today's appointments
    @Query("SELECT a FROM Appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.cabinet " +
           "WHERE a.doctor.userId = :doctorId " +
           "AND a.appointmentDateTime >= :dayStart " +
           "AND a.appointmentDateTime < :dayEnd " +
           "AND a.status NOT IN :excludedStatuses " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findTodayByDoctorUserId(
            @Param("doctorId") UUID doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd,
            @Param("excludedStatuses") Collection<AppointmentStatus> excludedStatuses
    );

    // Status update methods
    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.appointmentId = :appointmentId")
    int updateStatus(
            @Param("appointmentId") UUID appointmentId,
            @Param("status") AppointmentStatus status
    );

    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status, a.notes = :notes WHERE a.appointmentId = :appointmentId")
    int updateStatusWithNotes(
            @Param("appointmentId") UUID appointmentId,
            @Param("status") AppointmentStatus status,
            @Param("notes") String notes
    );

    // Ownership verification methods
    boolean existsByAppointmentIdAndDoctor_UserId(UUID appointmentId, UUID doctorId);

    boolean existsByAppointmentIdAndScheduledBySecretary_UserId(UUID appointmentId, UUID secretaryId);
}
