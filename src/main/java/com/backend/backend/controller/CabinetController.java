package com.backend.backend.controller;


import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.cabinet.CabinetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/office")
public class CabinetController {
    private final CabinetService cabinetService;

    public CabinetController(CabinetService cabinetService) {
        this.cabinetService = cabinetService;
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

}

