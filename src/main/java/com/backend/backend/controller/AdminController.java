package com.backend.backend.controller;

import com.backend.backend.dto.request.Admin.AdminAccResp;
import com.backend.backend.dto.request.Admin.AdminSignupRequest;
import com.backend.backend.dto.response.Admin.AdminResponse;
import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.enums.ApplicationStatus;
import com.backend.backend.service.Admin.AdminService;
import com.backend.backend.service.Admin.AdminDocAppRespService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminDocAppRespService appReponseService;

    public AdminController(AdminService adminService, AdminDocAppRespService appReponseService) {
        this.adminService = adminService;
        this.appReponseService = appReponseService;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AdminSignupRequest request
    ) throws Exception{
        AdminResponse response = adminService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changeStatus")
    public ResponseEntity<?> changeStatus(
            @Valid @RequestParam UUID applicationId,
            @Valid @RequestBody AdminAccResp adminResponse
    ) throws Exception{
        DoctorAppResponce responce = appReponseService.changeStatus(applicationId, adminResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(responce);
    }
}
