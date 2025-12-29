package com.backend.backend.controller;

import com.backend.backend.dto.request.Secretary.SecretarySignupRequest;
import com.backend.backend.dto.response.Secretary.SecretaryResponse;
import com.backend.backend.service.Secretary.SecretaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secretary")
public class SecretaryController {

    private final SecretaryService secretaryService;

    public SecretaryController(SecretaryService secretaryService) {
        this.secretaryService = secretaryService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SecretarySignupRequest request
    ) throws Exception{

        SecretaryResponse response = secretaryService.signUp(
                request.secretaryInfo(),
                request.credentials()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
