package com.backend.backend.controller;

import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.request.Doctor.DoctorInfoResponse;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Doctor.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    private final  DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.status(HttpStatus.OK).body(doctorService.getAllDoctors());
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
    @GetMapping("/check-cabinet")
    public ResponseEntity<?> checkCabinet(@AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {

            UUID doctorId = userDetails.getUserId();

            if (!doctorService.hasCabinet(doctorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "Cabinet required",
                                "redirectUrl", "/cabinet/create"
                        ));
            }

            return ResponseEntity.ok(Map.of("hasCabinet", true));
        }

        return ResponseEntity.ok().build();
    }
}