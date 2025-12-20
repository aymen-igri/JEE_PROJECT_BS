package com.backend.backend.controller;

import com.backend.backend.dto.request.Admin.AdminSignupRequest;
import com.backend.backend.dto.response.Admin.AdminResponse;
import com.backend.backend.service.Admin.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AdminSignupRequest request
    ) throws Exception{
        AdminResponse response = adminService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
