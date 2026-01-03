package com.backend.backend.service.Doctor;

import com.backend.backend.dto.request.Auth.AuthRequest;
import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.response.Doctor.DocComplete.DoctorAppResponce;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.practice.DoctorApplication;
import com.backend.backend.enums.ApplicationStatus;
import com.backend.backend.mapper.Doctor.DoctorAppMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import com.backend.backend.repository.user.DoctorRepository;
import com.backend.backend.repository.user.UserRepository;
import com.backend.backend.service.Email.EmailService;
import com.backend.backend.service.FileStorageService.FileStorageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class DoctorAppService {

    private final DoctorApplicationRepository doctorAppRepository;
    private final UserRepository userRepository;
    private final DoctorAppMapper doctorAppMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final Validator validator;
    private final ActivityLogRepository activityLogRepository;
    private final EmailService emailService;

    public DoctorAppService(
            DoctorApplicationRepository doctorAppRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            DoctorAppMapper doctorAppMapper,
            PasswordEncoder passwordEncoder,
            FileStorageService fileStorageService,
            Validator validator,
            ActivityLogRepository activityLogRepository,
            EmailService emailService
    ) {
        this.doctorAppRepository = doctorAppRepository;
        this.userRepository = userRepository;
        this.doctorAppMapper = doctorAppMapper;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = new FileStorageService();
        this.validator = validator;
        this.activityLogRepository = activityLogRepository;
        this.emailService = emailService;
    }

    @Transactional
    public DoctorAppResponce apply(
            DoctorAppDataRequest doctorAppRequest,
            AuthRequest authRequest,
            MultipartFile diploma,
            MultipartFile license,
            MultipartFile cv
    ){
        //aymen is here, i added this part so it will shows the exeption message form the validations in the records
        Set<ConstraintViolation<DoctorAppDataRequest>> doctorViolations = validator.validate(doctorAppRequest);
        if (!doctorViolations.isEmpty()) {
            throw new ConstraintViolationException(doctorViolations);
        }

        Set<ConstraintViolation<AuthRequest>> authViolations = validator.validate(authRequest);
        if (!authViolations.isEmpty()) {
            throw new ConstraintViolationException(authViolations);
        }

        if (userRepository.existsByEmail(doctorAppRequest.email())
                && doctorAppRepository.findDoctorApplicationByEmail(doctorAppRequest.email()) != null
                && doctorAppRepository.findDoctorApplicationByEmail(doctorAppRequest.email()).getStatus() != ApplicationStatus.REJECTED
        ){
            throw new IllegalArgumentException("Email already taken");
        }
        if (userRepository.existsByCIN(doctorAppRequest.CIN())
                && doctorAppRepository.findDoctorApplicationByCin(doctorAppRequest.CIN()) != null
                && doctorAppRepository.findDoctorApplicationByCin(doctorAppRequest.CIN()).getStatus() != ApplicationStatus.REJECTED)
        {
            throw new IllegalArgumentException("CIN already taken");
        }
        if (userRepository.existsByUsername(authRequest.username())
                && doctorAppRepository.findDoctorApplicationByUsername(authRequest.username()) != null
                && doctorAppRepository.findDoctorApplicationByUsername(authRequest.username()).getStatus() != ApplicationStatus.REJECTED
        ){
            throw new IllegalArgumentException("Username already taken");
        }
        if (doctorAppRepository.findDoctorApplicationByLicenseNumber(doctorAppRequest.licenseNumber()) != null
                && doctorAppRepository.findDoctorApplicationByLicenseNumber(doctorAppRequest.licenseNumber()).getStatus() != ApplicationStatus.REJECTED
        ){
            throw new IllegalArgumentException("License number already taken");
        }

        long maxFileSize = 10 * 1024 * 1024;
        if (diploma.getSize() > maxFileSize){
            throw new IllegalArgumentException("Diploma file size exceeds the maximum limit of 10MB");
        }
        if (license.getSize() > maxFileSize){
            throw new IllegalArgumentException("License file size exceeds the maximum limit of 10MB");
        }
        if (cv.getSize() > maxFileSize){
            throw new IllegalArgumentException("CV file size exceeds the maximum limit of 10MB");
        }

        DoctorApplication application = doctorAppMapper.toApplication(doctorAppRequest);

        application.setUsername(authRequest.username());
        application.setPassword(passwordEncoder.encode(authRequest.password()));

        UUID Idfiles = UUID.randomUUID();
        String diplomaPath = fileStorageService.storeFile(diploma, Idfiles, "diploma");
        String licensePath = fileStorageService.storeFile(license, Idfiles, "license");
        String cvPath = fileStorageService.storeFile(cv, Idfiles, "cv");

        application.setDiplomaDocument(diplomaPath);
        application.setLicenseDocument(licensePath);
        application.setCvDocument(cvPath);
        application.setApplicationDate(LocalDate.now());
        application.setStatus(ApplicationStatus.PENDING);

        // Save the application entity
        DoctorApplication savedApp = doctorAppRepository.save(application);

        // Log the application submission activity
        ActivityLog doctorLog = new ActivityLog();
        doctorLog.setAction("Application with ID: " + savedApp.getApplicationId() + " submitted.");
        doctorLog.setEntityType("DoctorApplication");
        doctorLog.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(doctorLog);

        // send a confirmation email
        emailService.setEmail(
                savedApp.getEmail(),
                "Welcome to Our Healthcare System",
                "Dear " + savedApp.getFullName() + ",\n\n" +
                        "Your Application has been successfully created.\n\n" +
                        "We will review your application and get back to you shortly.\n\n" +
                        "Best regards,\nIntegrity Healthcare Team"
        );

        return doctorAppMapper.toAppDTO(savedApp);
    }

    public List<DoctorAppResponce> getAllApplications(){
        return doctorAppRepository.findAll().stream().map(doctorAppMapper::toAppDTO).toList();
    }
}
