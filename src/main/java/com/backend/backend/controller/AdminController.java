package com.backend.backend.controller;

import com.backend.backend.dto.request.Admin.AdminAccResp;
import com.backend.backend.dto.request.Admin.AdminSignupRequest;
import com.backend.backend.dto.response.Admin.AdminResponse;
import com.backend.backend.dto.response.Doctor.DocComplete.DoctorAppResponce;
import com.backend.backend.repository.user.AdminRepository;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Admin.AdminDocAppRespService;
import com.backend.backend.service.Admin.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRepository adminRepository;
    private final AdminService adminService;
    private final AdminDocAppRespService appReponseService;

    public AdminController(
            AdminService adminService,
            AdminDocAppRespService appReponseService,
            AdminRepository adminRepository
    ) {
        this.adminRepository = adminRepository;
        this.adminService = adminService;
        this.appReponseService = appReponseService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmins() {
        return ResponseEntity.status(HttpStatus.OK).body(adminService.getAllAdmins());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AdminSignupRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception{
        UUID superAdminId = userDetails.getUser().getUserId();
        AdminResponse response = adminService.createAccount(request,superAdminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/changeStatus")
    public ResponseEntity<?> changeStatus(
            @Valid @RequestParam UUID applicationId,
            @Valid @RequestBody AdminAccResp adminResponse,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception{
        UUID adminId = userDetails.getUser().getUserId();
        DoctorAppResponce responce = appReponseService.changeStatus(applicationId, adminResponse, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responce);
    }
}
