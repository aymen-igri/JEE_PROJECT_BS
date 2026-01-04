package com.backend.backend.controller;

import com.backend.backend.dto.request.Auth.AuthRequest;
import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.response.Doctor.DocComplete.DoctorAppResponce;
import com.backend.backend.service.Doctor.DoctorAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/doctorApp")
public class DoctorAppController {

    private final DoctorAppService doctorAppService;
    private final ObjectMapper objectMapper;

    public DoctorAppController(DoctorAppService doctorAppService) {
        this.doctorAppService = doctorAppService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllApplications(){
        return ResponseEntity.status(HttpStatus.OK).body(doctorAppService.getAllApplications());
    }

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> apply(
            @RequestParam("application") String application,
            @RequestParam("auth") String authRequest,
            @RequestParam("diploma") MultipartFile diploma,
            @RequestParam("license") MultipartFile license,
            @RequestParam("cv") MultipartFile cv
    )throws Exception {

        DoctorAppDataRequest doctorAppRequest = objectMapper.readValue(application, DoctorAppDataRequest.class);
        AuthRequest authRequestObj = objectMapper.readValue(authRequest, AuthRequest.class);
        DoctorAppResponce responce = doctorAppService.apply(
                doctorAppRequest,
                authRequestObj,
                diploma,
                license,
                cv
        );

        return  ResponseEntity.status(HttpStatus.CREATED).body(responce);
    }

}
