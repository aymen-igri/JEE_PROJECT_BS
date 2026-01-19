package com.backend.backend.controller;

import com.backend.backend.dto.request.Billing.CreateBillingRequest;
import com.backend.backend.dto.response.Billing.BillingResponse;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Billing.BillingService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller for billing/payment management.
 * Billing is per appointment, not per consultation.
 * Only secretaries can create billings.
 * When payment is processed, appointment is automatically marked as COMPLETED.
 * Doctors can view their payment history.
 */
@RestController
@RequestMapping("/api/billings")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    /**
     * Creates a billing/receipt for an appointment.
     * Only secretaries can create billings.
     * When payment is processed, appointment is automatically marked as COMPLETED.
     */
    @PreAuthorize("hasRole('SECRETARY')")
    @PostMapping
    public ResponseEntity<BillingResponse> createBilling(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateBillingRequest request
    ) {
        UUID secretaryId = userDetails.getUser().getUserId();
        BillingResponse response = billingService.createBilling(secretaryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Gets a billing by ID.
     * Accessible by both secretaries and doctors.
     */
    @PreAuthorize("hasAnyRole('SECRETARY', 'DOCTOR')")
    @GetMapping("/{billingId}")
    public ResponseEntity<BillingResponse> getBillingById(
            @PathVariable UUID billingId
    ) {
        BillingResponse response = billingService.getBillingById(billingId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets billing by appointment ID.
     * Accessible by both secretaries and doctors.
     */
    @PreAuthorize("hasAnyRole('SECRETARY', 'DOCTOR')")
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<BillingResponse> getBillingByAppointmentId(
            @PathVariable UUID appointmentId
    ) {
        BillingResponse response = billingService.getBillingByAppointmentId(appointmentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all billings processed by the authenticated secretary.
     */
    @PreAuthorize("hasRole('SECRETARY')")
    @GetMapping("/my-billings")
    public ResponseEntity<List<BillingResponse>> getMyBillings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID secretaryId = userDetails.getUser().getUserId();
        List<BillingResponse> billings = billingService.getBillingsBySecretary(secretaryId);
        return ResponseEntity.ok(billings);
    }

    /**
     * Gets all billings for a cabinet within a date range.
     * Accessible by secretaries.
     */
    @PreAuthorize("hasRole('SECRETARY')")
    @GetMapping("/cabinet/{cabinetId}")
    public ResponseEntity<List<BillingResponse>> getBillingsByCabinet(
            @PathVariable UUID cabinetId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<BillingResponse> billings = billingService.getBillingsByCabinetAndDateRange(cabinetId, startDate, endDate);
        return ResponseEntity.ok(billings);
    }

    /**
     * Gets payment history for the authenticated doctor.
     * Doctors can view their own payment history.
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/history")
    public ResponseEntity<List<BillingResponse>> getDoctorPaymentHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        List<BillingResponse> billings = billingService.getBillingsByDoctorAndDateRange(doctorId, startDate, endDate);
        return ResponseEntity.ok(billings);
    }

    /**
     * Downloads the PDF receipt for a billing.
     * Accessible by both secretaries and doctors.
     */
    @PreAuthorize("hasAnyRole('SECRETARY', 'DOCTOR')")
    @GetMapping("/{billingId}/receipt")
    public ResponseEntity<Resource> downloadReceipt(
            @PathVariable UUID billingId
    ) {
        BillingResponse billing = billingService.getBillingById(billingId);

        if (billing.pdfPath() == null || billing.pdfPath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(billing.pdfPath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + billing.receiptNumber() + ".pdf\"")
                .body(resource);
    }
}

