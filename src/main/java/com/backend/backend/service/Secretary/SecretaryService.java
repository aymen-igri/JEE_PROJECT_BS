package com.backend.backend.service.Secretary;

import com.backend.backend.dto.request.Auth.AuthRequest;
import com.backend.backend.dto.request.Secretary.SecretaryRequest;
import com.backend.backend.dto.response.Secretary.SecretaryResponse;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.mapper.Secretary.SecretaryMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.user.SecretaryRepository;
import com.backend.backend.repository.user.UserRepository;
import com.backend.backend.service.Email.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecretaryService {

    private final SecretaryRepository secretaryRepository;
    private final SecretaryMapper secretaryMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final EmailService emailService;

    public SecretaryService(
            SecretaryRepository secretaryRepository,
            SecretaryMapper secretaryMapper,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository,
            EmailService emailService
    ) {
        this.secretaryRepository = secretaryRepository;
        this.secretaryMapper = secretaryMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.emailService = emailService;
    }

    @Transactional
    public List<SecretaryResponse> getAllSecretaries() {
        List<Secretary> secretaries = secretaryRepository.findAll();
        return secretaries.stream()
                .map(secretaryMapper::toSecretaryDTO)
                .toList();
    }

    @Transactional
    public SecretaryResponse signUp(
            SecretaryRequest secretaryRequest,
            AuthRequest authRequest
    ) {
        if (userRepository.existsByUsername(authRequest.username())){
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(secretaryRequest.email())){
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByCIN(secretaryRequest.CIN())){
            throw new IllegalArgumentException("CIN already exists");
        }

        Secretary secretary = secretaryMapper.toSecretary(secretaryRequest);

        secretary.setUsername(authRequest.username());
        secretary.setPassword(passwordEncoder.encode(authRequest.password()));

        // Save the secretary entity
        Secretary savedSecretary = secretaryRepository.save(secretary);

        // Log the creation activity
        ActivityLog secretaryLog = new ActivityLog();
        secretaryLog.setAction("Secretary account with ID: " + savedSecretary.getUserId() + "created." ) ;
        secretaryLog.setEntityType("Secretary");
        secretaryLog.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(secretaryLog);

        // Send welcome email
        emailService.setEmail(
                secretaryRequest.email(),
                "Welcome to Our Healthcare System",
                "Dear " + secretaryRequest.fullName() + ",\n\n" +
                        "Your secretary account has been successfully created.\n\n" +
                        "Best regards,\nIntegrity Healthcare Team"
        );

        return secretaryMapper.toSecretaryDTO(savedSecretary);
    }

    // Statistics methods for dashboard
    public long getTotalSecretariesCount() {
        return secretaryRepository.count();
    }

    public long getActiveSecretariesCount() {
        return secretaryRepository.countByStatus(com.backend.backend.enums.EStatus.ACTIVE);
    }

    public long getInactiveSecretariesCount() {
        return secretaryRepository.countByStatus(com.backend.backend.enums.EStatus.INACTIVE);
    }
}
