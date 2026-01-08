package com.backend.backend.repository.billing;

import com.backend.backend.entity.patient.AppointmentBilling;
import com.backend.backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for AppointmentBilling entity.
 * Provides queries for billing/receipt management.
 * Billing is per appointment, not per consultation.
 */
@Repository
public interface AppointmentBillingRepository extends JpaRepository<AppointmentBilling, UUID> {

    /**
     * Find billing by ID with all details fetched.
     */
    @Query("SELECT ab FROM AppointmentBilling ab " +
           "JOIN FETCH ab.appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.doctor " +
           "JOIN FETCH a.cabinet " +
           "JOIN FETCH ab.processedBy " +
           "WHERE ab.billingId = :id")
    Optional<AppointmentBilling> findByIdWithDetails(@Param("id") UUID id);

    /**
     * Find billing by appointment ID.
     */
    @Query("SELECT ab FROM AppointmentBilling ab " +
           "JOIN FETCH ab.appointment a " +
           "JOIN FETCH ab.processedBy " +
           "WHERE a.appointmentId = :appointmentId")
    Optional<AppointmentBilling> findByAppointmentId(@Param("appointmentId") UUID appointmentId);

    /**
     * Check if billing exists for an appointment.
     */
    boolean existsByAppointmentAppointmentId(UUID appointmentId);

    /**
     * Find billing by receipt number.
     */
    Optional<AppointmentBilling> findByReceiptNumber(String receiptNumber);

    /**
     * Find all billings processed by a specific secretary.
     */
    @Query("SELECT ab FROM AppointmentBilling ab " +
           "JOIN FETCH ab.appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.cabinet " +
           "WHERE ab.processedBy.userId = :secretaryId " +
           "ORDER BY ab.paymentDate DESC")
    List<AppointmentBilling> findByProcessedByUserId(@Param("secretaryId") UUID secretaryId);

    /**
     * Find all billings for a specific cabinet within a date range.
     */
    @Query("SELECT ab FROM AppointmentBilling ab " +
           "JOIN FETCH ab.appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH ab.processedBy " +
           "WHERE a.cabinet.cabinetId = :cabinetId " +
           "AND ab.paymentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ab.paymentDate DESC")
    List<AppointmentBilling> findByCabinetIdAndDateRange(
            @Param("cabinetId") UUID cabinetId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find all billings for a specific doctor within a date range.
     */
    @Query("SELECT ab FROM AppointmentBilling ab " +
           "JOIN FETCH ab.appointment a " +
           "JOIN FETCH a.patient " +
           "JOIN FETCH a.cabinet " +
           "JOIN FETCH ab.processedBy " +
           "WHERE a.doctor.userId = :doctorId " +
           "AND ab.paymentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY ab.paymentDate DESC")
    List<AppointmentBilling> findByDoctorIdAndDateRange(
            @Param("doctorId") UUID doctorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get count of billings by status for a cabinet.
     */
    @Query("SELECT COUNT(ab) FROM AppointmentBilling ab " +
           "JOIN ab.appointment a " +
           "WHERE a.cabinet.cabinetId = :cabinetId " +
           "AND ab.paymentStatus = :status")
    long countByCabinetIdAndStatus(
            @Param("cabinetId") UUID cabinetId,
            @Param("status") PaymentStatus status
    );

    /**
     * Get count of billings today for receipt number generation.
     */
    @Query("SELECT COUNT(ab) FROM AppointmentBilling ab " +
           "WHERE ab.paymentDate >= :startOfDay")
    long countBillingsToday(@Param("startOfDay") LocalDateTime startOfDay);
}
