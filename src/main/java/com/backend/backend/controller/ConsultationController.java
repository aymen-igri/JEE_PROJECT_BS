package com.backend.backend.controller;

import com.backend.backend.dto.request.Consultation.CreateConsultationRequest;
import com.backend.backend.dto.request.Consultation.UpdateConsultationRequest;
import com.backend.backend.dto.response.Consultation.ConsultationDetailResponse;
import com.backend.backend.dto.response.Consultation.ConsultationResponse;
import com.backend.backend.enums.ConsultationStatus;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Consultation.ConsultationService;
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

import java.util.UUID;

/**
 * REST Controller for Consultation management.
 * Consultations are created via patient ID and have no date.
 * A consultation can span multiple appointments.
 * All endpoints require DOCTOR role.
 */
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    /**
     * Creates a new consultation for a patient.
     * Consultations have no date - they are tracked only by status.
     * POST /api/consultations
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<ConsultationResponse> createConsultation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateConsultationRequest request
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        ConsultationResponse response = consultationService.createConsultation(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing consultation.
     * PUT /api/consultations/{id}
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ConsultationResponse> updateConsultation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateConsultationRequest request
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        ConsultationResponse response = consultationService.updateConsultation(doctorId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a consultation by ID.
     * GET /api/consultations/{id}
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ConsultationResponse> getConsultationById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        ConsultationResponse response = consultationService.getConsultationById(doctorId, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a consultation with full details (diagnostics, prescriptions).
     * GET /api/consultations/{id}/detail
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/{id}/detail")
    public ResponseEntity<ConsultationDetailResponse> getConsultationDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        ConsultationDetailResponse response = consultationService.getConsultationDetail(doctorId, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets paginated list of doctor's consultations.
     * GET /api/consultations?page=0&size=10&sort=createdAt,desc
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping
    public ResponseEntity<Page<ConsultationResponse>> getMyConsultations(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConsultationResponse> response = consultationService.getMyConsultations(doctorId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets consultations for a specific patient.
     * GET /api/consultations/patient/{patientId}?page=0&size=10
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<ConsultationResponse>> getConsultationsByPatient(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ConsultationResponse> response = consultationService.getConsultationsByPatient(doctorId, patientId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets consultations by status.
     * GET /api/consultations/status/{status}?page=0&size=10
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ConsultationResponse>> getConsultationsByStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable ConsultationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ConsultationResponse> response = consultationService.getConsultationsByStatus(doctorId, status, pageable);
        return ResponseEntity.ok(response);
    }
}


