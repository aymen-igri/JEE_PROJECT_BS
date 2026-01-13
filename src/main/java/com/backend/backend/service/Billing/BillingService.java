package com.backend.backend.service.Billing;

import com.backend.backend.dto.request.Billing.CreateBillingRequest;
import com.backend.backend.dto.response.Billing.BillingResponse;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.entity.patient.AppointmentBilling;
import com.backend.backend.enums.AppointmentStatus;
import com.backend.backend.enums.PaymentStatus;
import com.backend.backend.mapper.Billing.BillingMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.billing.AppointmentBillingRepository;
import com.backend.backend.repository.patient.AppointmentRepository;
import com.backend.backend.repository.user.SecretaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing appointment billing/payments.
 * Billing is per appointment, not per consultation.
 * Only secretaries can create and manage billings.
 * When payment is processed, the appointment is automatically marked as COMPLETED.
 */
@Service
public class BillingService {

    private final AppointmentBillingRepository billingRepository;
    private final AppointmentRepository appointmentRepository;
    private final SecretaryRepository secretaryRepository;
    private final BillingMapper billingMapper;
    private final ReceiptPdfService receiptPdfService;
    private final ActivityLogRepository activityLogRepository;

    public BillingService(
            AppointmentBillingRepository billingRepository,
            AppointmentRepository appointmentRepository,
            SecretaryRepository secretaryRepository,
            BillingMapper billingMapper,
            ReceiptPdfService receiptPdfService,
            ActivityLogRepository activityLogRepository
    ) {
        this.billingRepository = billingRepository;
        this.appointmentRepository = appointmentRepository;
        this.secretaryRepository = secretaryRepository;
        this.billingMapper = billingMapper;
        this.receiptPdfService = receiptPdfService;
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Creates a billing/receipt for an appointment.
     * Can only be done by a secretary.
     * When payment is processed, the appointment is automatically marked as COMPLETED.
     *
     * @param secretaryId The ID of the secretary processing the payment
     * @param request The billing request containing payment details
     * @return The created billing response
     */
    @Transactional
    public BillingResponse createBilling(UUID secretaryId, CreateBillingRequest request) {
        // Validate secretary exists
        Secretary secretary = secretaryRepository.findByUserId(secretaryId);
        if (secretary == null) {
            throw new IllegalArgumentException("Secretary not found with ID: " + secretaryId);
        }

        // Fetch appointment with details
        Appointment appointment = appointmentRepository.findByAppointmentIdWithDetails(request.appointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + request.appointmentId()));

        // Check if billing already exists for this appointment
        if (billingRepository.existsByAppointmentAppointmentId(request.appointmentId())) {
            throw new IllegalArgumentException("Billing already exists for this appointment");
        }

        // Validate appointment status - cannot bill cancelled or no-show appointments
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
            appointment.getStatus() == AppointmentStatus.NO_SHOW) {
            throw new IllegalArgumentException("Cannot create billing for appointment with status: " + appointment.getStatus());
        }

        // Calculate amounts
        BigDecimal originalPrice = appointment.getPrice();
        if (originalPrice == null) {
            // Use cabinet default price if no price set on appointment
            originalPrice = appointment.getCabinet().getDefaultConsultPrice();
            if (originalPrice == null) {
                throw new IllegalArgumentException("No price set for appointment and no default cabinet price configured");
            }
        }

        BigDecimal discountAmount = request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO;

        // Validate discount doesn't exceed original price
        if (discountAmount.compareTo(originalPrice) > 0) {
            throw new IllegalArgumentException("Discount amount cannot exceed original price");
        }

        BigDecimal finalAmount = originalPrice.subtract(discountAmount);

        // Generate receipt number
        String receiptNumber = generateReceiptNumber();

        // Create billing entity
        AppointmentBilling billing = AppointmentBilling.builder()
                .appointment(appointment)
                .processedBy(secretary)
                .receiptNumber(receiptNumber)
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .discountReason(request.discountReason())
                .finalAmount(finalAmount)
                .paymentType(request.paymentType())
                .paymentStatus(PaymentStatus.PAID)
                .paymentDate(LocalDateTime.now())
                .notes(request.notes())
                .build();

        // Save billing
        AppointmentBilling savedBilling = billingRepository.save(billing);

        // Update appointment payment status and mark as COMPLETED when payment is processed
        appointment.setPaymentStatus(PaymentStatus.PAID);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // Generate PDF receipt
        try {
            String pdfPath = receiptPdfService.generateReceipt(savedBilling);
            savedBilling.setPdfPath(pdfPath);
            savedBilling = billingRepository.save(savedBilling);
        } catch (IOException e) {
            // Log error but don't fail the transaction - PDF generation is secondary
            logActivity("Billing PDF generation failed", "AppointmentBilling", savedBilling.getBillingId(),
                    "Failed to generate PDF for billing: " + e.getMessage());
        }

        // Log activity
        logActivity("Billing created", "AppointmentBilling", savedBilling.getBillingId(),
                "Billing created for appointment. Receipt: " + receiptNumber +
                ", Amount: " + finalAmount + " MAD, Processed by: " + secretary.getFullName());

        // Fetch with all details for response
        AppointmentBilling billingWithDetails = billingRepository.findByIdWithDetails(savedBilling.getBillingId())
                .orElse(savedBilling);

        return billingMapper.toBillingResponse(billingWithDetails);
    }

    /**
     * Gets a billing by ID.
     */
    @Transactional(readOnly = true)
    public BillingResponse getBillingById(UUID billingId) {
        AppointmentBilling billing = billingRepository.findByIdWithDetails(billingId)
                .orElseThrow(() -> new IllegalArgumentException("Billing not found with ID: " + billingId));
        return billingMapper.toBillingResponse(billing);
    }

    /**
     * Gets billing by appointment ID.
     */
    @Transactional(readOnly = true)
    public BillingResponse getBillingByAppointmentId(UUID appointmentId) {
        AppointmentBilling billing = billingRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Billing not found for appointment ID: " + appointmentId));

        // Fetch with all details
        billing = billingRepository.findByIdWithDetails(billing.getBillingId())
                .orElse(billing);

        return billingMapper.toBillingResponse(billing);
    }

    /**
     * Gets all billings processed by a secretary.
     */
    @Transactional(readOnly = true)
    public List<BillingResponse> getBillingsBySecretary(UUID secretaryId) {
        // Validate secretary exists
        if (secretaryRepository.findByUserId(secretaryId) == null) {
            throw new IllegalArgumentException("Secretary not found with ID: " + secretaryId);
        }

        return billingRepository.findByProcessedByUserId(secretaryId).stream()
                .map(billingMapper::toBillingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets all billings for a cabinet within a date range.
     */
    @Transactional(readOnly = true)
    public List<BillingResponse> getBillingsByCabinetAndDateRange(UUID cabinetId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return billingRepository.findByCabinetIdAndDateRange(cabinetId, startDateTime, endDateTime).stream()
                .map(billingMapper::toBillingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets all billings for a doctor within a date range.
     * Doctors can view their payment history.
     */
    @Transactional(readOnly = true)
    public List<BillingResponse> getBillingsByDoctorAndDateRange(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return billingRepository.findByDoctorIdAndDateRange(doctorId, startDateTime, endDateTime).stream()
                .map(billingMapper::toBillingResponse)
                .collect(Collectors.toList());
    }

    /**
     * Generates a unique receipt number.
     * Format: REC-YYYYMMDD-XXXX (where XXXX is a daily sequence)
     */
    private String generateReceiptNumber() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Get count of billings today
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        long todayCount = billingRepository.countBillingsToday(startOfDay);

        String sequencePart = String.format("%04d", todayCount + 1);

        return "REC-" + datePart + "-" + sequencePart;
    }

    private void logActivity(String action, String entityType, UUID entityId, String details) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId != null ? entityId.toString() : null);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }
}

