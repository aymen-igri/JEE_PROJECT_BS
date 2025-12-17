package com.backend.backend.controller;

import com.backend.backend.dto.request.Auth.AuthRequest;
import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.request.Doctor.DoctorAppRequest;
import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.service.Doctor.DoctorAppService;
import com.backend.backend.service.Doctor.DoctorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import javax.print.attribute.standard.Media;
import java.awt.*;

@RestController
public class DoctorAppController {

    private final DoctorAppService doctorAppService;
    private final ObjectMapper objectMapper;

    public DoctorAppController(DoctorAppService doctorAppService) {
        this.doctorAppService = doctorAppService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping(value = "/api/doctor/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DoctorAppResponce> apply(
            @RequestParam("application") String application,
            @RequestParam("auth") String authRequest,
            @RequestParam("diploma") MultipartFile diploma,
            @RequestParam("license") MultipartFile license,
            @RequestParam("cv") MultipartFile cv
    )throws JsonProcessingException {

        DoctorAppDataRequest doctorAppRequest = objectMapper.readValue(application, DoctorAppDataRequest.class);
        AuthRequest authRequestObj = objectMapper.readValue(authRequest, AuthRequest.class);
        DoctorAppResponce responce = doctorAppService.apply(
                doctorAppRequest,
                authRequestObj,
                diploma,
                license,
                cv
        );
        return  ResponseEntity.ok(responce);
    }

}
