package com.backend.backend.controller;


import com.backend.backend.dto.response.Doctor.DoctorDashboardDTO;
import com.backend.backend.service.Doctor.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorDashboardController {

    private final DashboardService dashboardService;

    public DoctorDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<DoctorDashboardDTO> getDoctorDashboard(@PathVariable UUID doctorId) {
        DoctorDashboardDTO dashboard = dashboardService.getDoctorDashboard(doctorId);
        return ResponseEntity.ok(dashboard);
    }
}


