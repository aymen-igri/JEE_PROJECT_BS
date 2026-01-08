package com.backend.backend.controller;

import com.backend.backend.dto.request.Consultation.CreateDiagnosticRequest;
import com.backend.backend.dto.request.Consultation.UpdateDiagnosticRequest;
import com.backend.backend.dto.response.Consultation.DiagnosticResponse;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Consultation.DiagnosticService;
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
 * REST Controller for Diagnostic management.
 * All endpoints require DOCTOR role.
 * Updates are subject to grace period restrictions.
 */
@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    public DiagnosticController(DiagnosticService diagnosticService) {
        this.diagnosticService = diagnosticService;
    }

    /**
     * Creates a new diagnostic.
     * POST /api/diagnostics
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<DiagnosticResponse> createDiagnostic(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateDiagnosticRequest request
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        DiagnosticResponse response = diagnosticService.createDiagnostic(doctorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing diagnostic.
     * Only allowed within the grace period after creation.
     * PUT /api/diagnostics/{id}
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<DiagnosticResponse> updateDiagnostic(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDiagnosticRequest request
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        DiagnosticResponse response = diagnosticService.updateDiagnostic(doctorId, id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a diagnostic by ID.
     * GET /api/diagnostics/{id}
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<DiagnosticResponse> getDiagnosticById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        DiagnosticResponse response = diagnosticService.getDiagnosticById(doctorId, id);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all diagnostics for a consultation.
     * GET /api/diagnostics/consultation/{consultationId}
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<List<DiagnosticResponse>> getDiagnosticsByConsultation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID consultationId
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        List<DiagnosticResponse> response = diagnosticService.getDiagnosticsByConsultation(doctorId, consultationId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets diagnostics for a patient.
     * GET /api/diagnostics/patient/{patientId}?page=0&size=10
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<DiagnosticResponse>> getDiagnosticsByPatient(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DiagnosticResponse> response = diagnosticService.getDiagnosticsByPatient(doctorId, patientId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if a diagnostic can still be modified (within grace period).
     * GET /api/diagnostics/{id}/can-modify
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/{id}/can-modify")
    public ResponseEntity<Boolean> canModifyDiagnostic(
            @PathVariable UUID id
    ) {
        boolean canModify = diagnosticService.canModifyDiagnostic(id);
        return ResponseEntity.ok(canModify);
    }
}

