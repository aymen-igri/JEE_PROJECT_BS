package com.backend.backend.controller;

import com.backend.backend.dto.request.Consultation.CreatePrescriptionRequest;
import com.backend.backend.dto.response.Consultation.PrescriptionResponse;
import com.backend.backend.entity.perscription.Medicament;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Consultation.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Prescription management.
 * All endpoints require DOCTOR role.
 * NOTE: Prescriptions are immutable - no update or delete endpoints.
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    /**
     * Creates a new prescription with items and analyses.
     * POST /api/prescriptions
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePrescriptionRequest request
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        PrescriptionResponse response = prescriptionService.createPrescription(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Gets a prescription by ID with items and analyses.
     * GET /api/prescriptions/{id}
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        PrescriptionResponse response = prescriptionService.getPrescriptionById(doctorId, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all prescriptions for a consultation.
     * GET /api/prescriptions/consultation/{consultationId}
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByConsultation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID consultationId
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        List<PrescriptionResponse> response = prescriptionService.getPrescriptionsByConsultation(doctorId, consultationId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets prescriptions for a patient.
     * GET /api/prescriptions/patient/{patientId}?page=0&size=10
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<PrescriptionResponse>> getPrescriptionsByPatient(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("prescribedDate").descending());
        Page<PrescriptionResponse> response = prescriptionService.getPrescriptionsByPatient(doctorId, patientId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Searches medicaments by name for prescription selection.
     * GET /api/prescriptions/medicaments/search?query=paracetamol&page=0&size=20
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/medicaments/search")
    public ResponseEntity<Page<Medicament>> searchMedicaments(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Medicament> response = prescriptionService.searchMedicaments(query, pageable);
        return ResponseEntity.ok(response);
    }
}

