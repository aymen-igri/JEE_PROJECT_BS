package com.backend.backend.controller;

import com.backend.backend.dto.request.Patient.CreatePatientRequest;
import com.backend.backend.dto.response.Patient.CreatePatientResponse;
import com.backend.backend.service.Patient.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @PostMapping("/create")
    public ResponseEntity<?> createPatinet(
            @Valid @RequestBody CreatePatientRequest request
    ) throws Exception{
        CreatePatientResponse response = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
