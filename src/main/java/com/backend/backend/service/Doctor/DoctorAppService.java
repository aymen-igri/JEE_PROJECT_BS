package com.backend.backend.service.Doctor;

import com.backend.backend.dto.request.Auth.AuthRequest;
import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.request.Doctor.DoctorAppRequest;
import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.entity.practice.DoctorApplication;
import com.backend.backend.enums.ApplicationStatus;
import com.backend.backend.mapper.Doctor.DoctorAppMapper;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import com.backend.backend.service.FileStorageService.FileStorageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DoctorAppService {

    private final DoctorApplicationRepository doctorAppRepository;
    private final DoctorAppMapper doctorAppMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public DoctorAppService(DoctorApplicationRepository doctorAppRepository, DoctorAppMapper doctorAppMapper, PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.doctorAppRepository = doctorAppRepository;
        this.doctorAppMapper = doctorAppMapper;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = new FileStorageService();
    }

    public DoctorAppResponce apply(DoctorAppDataRequest doctorAppRequest,
                                   AuthRequest authRequest,
                                   MultipartFile diploma,
                                   MultipartFile license,
                                   MultipartFile cv
    ){

        DoctorApplication application = doctorAppMapper.toApplication(doctorAppRequest);

        application.setUsername(authRequest.username());
        application.setPassword(passwordEncoder.encode(authRequest.password()));
        application.setApplicationDate(LocalDate.now());
        application.setStatus(ApplicationStatus.PENDING);

        DoctorApplication savedApp = doctorAppRepository.save(application);

        String diplomaPath = fileStorageService.storeFile(diploma, savedApp.getApplicationId(), "diploma");
        String licensePath = fileStorageService.storeFile(license, savedApp.getApplicationId(), "license");
        String cvPath = fileStorageService.storeFile(cv, savedApp.getApplicationId(), "cv");

        savedApp.setDiplomaDocument(diplomaPath);
        savedApp.setLicenseDocument(licensePath);
        savedApp.setCvDocument(cvPath);

        DoctorApplication updatedApp = doctorAppRepository.save(savedApp);

        return doctorAppMapper.toAppDTO(updatedApp);
    }
}
