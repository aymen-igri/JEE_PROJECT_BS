package com.backend.backend.controller;

import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.request.Doctor.DoctorInfoResponse;
import com.backend.backend.service.Doctor.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    private final  DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/me")
    public ResponseEntity<DoctorInfoResponse> getMyInfo(Principal principal) {
        DoctorInfoResponse doctor = doctorService.getDoctorByUsername(principal.getName());
        return ResponseEntity.ok(doctor);
    }

    @PutMapping("/me")
    public ResponseEntity<DoctorInfoResponse> updateMyInfo(
            Principal principal,
            @Valid @RequestBody DoctorAppDataRequest request) {
        DoctorInfoResponse updated = doctorService.updateDoctor(principal.getName(), request);
        return ResponseEntity.ok(updated);
    }
}