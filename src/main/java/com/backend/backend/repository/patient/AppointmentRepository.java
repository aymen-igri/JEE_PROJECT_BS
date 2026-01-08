package com.backend.backend.repository.Patient;

import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Find current appointment (happening now - IN_PROGRESS status)
    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient p " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.cabinet " +
            "WHERE a.doctor.userId = :doctorId " +
            "AND a.status = :status " +
            "AND a.appointmentDateTime <= :now " +
            "ORDER BY a.appointmentDateTime DESC")
    Optional<Appointment> findCurrentAppointment(
            @Param("doctorId") UUID doctorId,
            @Param("status") AppointmentStatus status,
            @Param("now") LocalDateTime now
    );

    // Find next upcoming appointments
    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient p " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.cabinet " +
            "WHERE a.doctor.userId = :doctorId " +
            "AND a.status IN :statuses " +
            "AND a.appointmentDateTime > :now " +
            "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointments(
            @Param("doctorId") UUID doctorId,
            @Param("statuses") Set<AppointmentStatus> statuses,
            @Param("now") LocalDateTime now
    );

    // Existing methods from your AppointmentService
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.userId = :doctorId " +
            "AND a.appointmentDateTime >= :rangeStart " +
            "AND a.appointmentDateTime < :rangeEnd " +
            "AND a.status NOT IN :excludedStatuses")
    List<Appointment> findPotentialConflicts(
            @Param("doctorId") UUID doctorId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("excludedStatuses") List<String> excludedStatuses
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.cabinet.cabinetId = :cabinetId " +
            "AND a.appointmentDateTime >= :rangeStart " +
            "AND a.appointmentDateTime < :rangeEnd " +
            "AND a.status NOT IN :excludedStatuses")
    List<Appointment> findCabinetConflicts(
            @Param("cabinetId") UUID cabinetId,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("excludedStatuses") List<String> excludedStatuses
    );

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.cabinet " +
            "LEFT JOIN FETCH a.scheduledBySecretary " +
            "WHERE a.appointmentId = :appointmentId")
    Optional<Appointment> findByAppointmentIdWithDetails(@Param("appointmentId") UUID appointmentId);

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.cabinet " +
            "WHERE a.scheduledBySecretary.userId = :secretaryId " +
            "AND (:excludedStatuses IS NULL OR a.status NOT IN :excludedStatuses)")
    List<Appointment> findByScheduledBySecretaryUserIdWithDetails(
            @Param("secretaryId") UUID secretaryId,
            @Param("excludedStatuses") Set<AppointmentStatus> excludedStatuses
    );

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.cabinet " +
            "WHERE a.doctor.userId = :doctorId " +
            "AND a.appointmentDateTime > :now " +
            "AND a.status IN :statuses " +
            "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingByDoctorUserId(
            @Param("doctorId") UUID doctorId,
            @Param("now") LocalDateTime now,
            @Param("statuses") Set<AppointmentStatus> statuses
    );

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.cabinet " +
            "WHERE a.doctor.userId = :doctorId " +
            "AND a.appointmentDateTime >= :dayStart " +
            "AND a.appointmentDateTime <= :dayEnd " +
            "AND a.status NOT IN :excludedStatuses " +
            "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findTodayByDoctorUserId(
            @Param("doctorId") UUID doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd,
            @Param("excludedStatuses") Set<AppointmentStatus> excludedStatuses
    );

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient " +
            "LEFT JOIN FETCH a.doctor " +
            "LEFT JOIN FETCH a.cabinet " +
            "WHERE a.patient.patientId = :patientId " +
            "AND (:excludedStatuses IS NULL OR a.status NOT IN :excludedStatuses)")
    List<Appointment> findByPatientPatientIdWithDetails(
            @Param("patientId") UUID patientId,
            @Param("excludedStatuses") Set<AppointmentStatus> excludedStatuses
    );
}