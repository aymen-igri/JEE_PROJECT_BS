package com.backend.backend.controller;


import com.backend.backend.dto.response.Cabinet.CabinetResponse;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.cabinet.CabinetService;
import com.backend.backend.service.subscription.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/office")
public class CabinetController {
    private final CabinetService cabinetService;
    private final SubscriptionService subscriptionService;

    public CabinetController(CabinetService cabinetService, SubscriptionService subscriptionService) {
        this.cabinetService = cabinetService;
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Cabinet> createCabinet(@Valid @RequestBody Cabinet cabinet, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID doctorId = userDetails.getUserId();
        Cabinet created = cabinetService.createCabinet(cabinet,doctorId);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCabinets() {
        return ResponseEntity.status(HttpStatus.OK).body(cabinetService.getAllCabinets());
    }
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getOfficeInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        // Get the cabinet information for the authenticated user
        CabinetResponse cabinet = cabinetService.getCabinetByAuthenticatedUser(authentication);

        // Map the response to match frontend expectations
        Map<String, Object> response = new HashMap<>();
        response.put("cabinetId", cabinet.id().toString());
        response.put("title", cabinet.name());
        response.put("name", cabinet.name());
        response.put("description", cabinet.description());
        response.put("specialty", cabinet.specialty());
        response.put("phone", cabinet.phone());
        response.put("address", cabinet.address()); // Add address field to CabinetResponse if needed
        response.put("status", cabinet.status());
        response.put("doctorName", cabinet.doctorName()); // Add doctor name field if needed
        response.put("consultationPrice", cabinet.defaultConsultPrice());
        response.put("createdAt", cabinet.createdAt());
        response.put("accountStatus", cabinet.status());
        response.put("logoUrl", cabinet.logo() != null ? "/uploads/logos" + cabinet.logo() : null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-logo")
    public ResponseEntity<Map<String, String>> uploadLogo(
            @RequestParam("file") MultipartFile file) throws IOException {

        // Save file to disk or cloud storage
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get("uploads/logos/", filename);
        Files.createDirectories(filepath.getParent());
        Files.write(filepath, file.getBytes());

        // Return the URL
        String url = "/uploads/logos/" + filename;
        return ResponseEntity.ok(Map.of("url", url));
    }
    @GetMapping("/check-subscription")
    public ResponseEntity<Map<String, Object>> checkSubscription(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {

            UUID doctorId = userDetails.getUserId();
            UUID cabinetId = cabinetService.getActiveCabinetOrThrow(doctorId).getCabinetId();

            if (subscriptionService.needsSubscription(cabinetId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "Subscription required",
                                "redirectUrl", "/subscribe",
                                "cabinetId", cabinetId
                        ));
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }

}

