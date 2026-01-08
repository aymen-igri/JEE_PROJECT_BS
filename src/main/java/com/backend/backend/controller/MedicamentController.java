package com.backend.backend.controller;

import com.backend.backend.dto.request.Medicament.CreateMedicamentRequest;
import com.backend.backend.dto.request.Medicament.UpdateMedicamentRequest;
import com.backend.backend.dto.response.Medicament.MedicamentResponse;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Medicament.MedicamentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for medicament management.
 * Only admins and super admins can create, update, and deactivate medicaments.
 */
@RestController
@RequestMapping("/api/admin/medicaments")
public class MedicamentController {

    private final MedicamentService medicamentService;

    public MedicamentController(MedicamentService medicamentService) {
        this.medicamentService = medicamentService;
    }

    /**
     * Creates a new medicament.
     * POST /api/admin/medicaments
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<MedicamentResponse> createMedicament(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateMedicamentRequest request
    ) {
        UUID adminId = userDetails.getUser().getUserId();
        MedicamentResponse response = medicamentService.createMedicament(adminId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing medicament.
     * PUT /api/admin/medicaments/{medicamentId}
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{medicamentId}")
    public ResponseEntity<MedicamentResponse> updateMedicament(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID medicamentId,
            @Valid @RequestBody UpdateMedicamentRequest request
    ) {
        UUID adminId = userDetails.getUser().getUserId();
        MedicamentResponse response = medicamentService.updateMedicament(adminId, medicamentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates a medicament (soft delete).
     * PUT /api/admin/medicaments/{medicamentId}/deactivate
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{medicamentId}/deactivate")
    public ResponseEntity<MedicamentResponse> deactivateMedicament(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID medicamentId
    ) {
        UUID adminId = userDetails.getUser().getUserId();
        MedicamentResponse response = medicamentService.deactivateMedicament(adminId, medicamentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reactivates a medicament.
     * PUT /api/admin/medicaments/{medicamentId}/reactivate
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{medicamentId}/reactivate")
    public ResponseEntity<MedicamentResponse> reactivateMedicament(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID medicamentId
    ) {
        UUID adminId = userDetails.getUser().getUserId();
        MedicamentResponse response = medicamentService.reactivateMedicament(adminId, medicamentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a medicament by ID.
     * GET /api/admin/medicaments/{medicamentId}
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{medicamentId}")
    public ResponseEntity<MedicamentResponse> getMedicamentById(
            @PathVariable UUID medicamentId
    ) {
        MedicamentResponse response = medicamentService.getMedicamentById(medicamentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all medicaments with pagination.
     * GET /api/admin/medicaments?page=0&size=20&includeInactive=false
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<MedicamentResponse>> getAllMedicaments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        Page<MedicamentResponse> medicaments = medicamentService.getAllMedicaments(page, size, includeInactive);
        return ResponseEntity.ok(medicaments);
    }

    /**
     * Searches medicaments by name.
     * GET /api/admin/medicaments/search?query=paracetamol
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<MedicamentResponse>> searchMedicaments(
            @RequestParam String query
    ) {
        List<MedicamentResponse> medicaments = medicamentService.searchMedicaments(query);
        return ResponseEntity.ok(medicaments);
    }
}

