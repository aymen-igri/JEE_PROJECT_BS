package com.backend.backend.controller;

import com.backend.backend.dto.request.SuperAdmin.SuperAdminRequest;
import com.backend.backend.dto.response.SuperAdmin.SuperAdminResponse;
import com.backend.backend.entity.User.SuperAdmin;
import com.backend.backend.repository.user.SuperAdminRepository;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.SuperAdmin.SuperAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/superAdmin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final SuperAdminRepository superAdminRepository;

    public SuperAdminController(
            SuperAdminService superAdminService,
            SuperAdminRepository superAdminRepository
    ) {
        this.superAdminService = superAdminService;
        this.superAdminRepository = superAdminRepository;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<?> getMainUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        return ResponseEntity.ok(superAdminRepository.findById(userDetails.getUserId()));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/updateInfo")
    public ResponseEntity<?> updateSuperAdminInfo(
            @RequestBody SuperAdminRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws Exception {
        SuperAdminResponse response = superAdminService.updateSuperAdminProfile(request,userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
