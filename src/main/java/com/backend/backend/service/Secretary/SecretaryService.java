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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SecretaryService {

    private final SecretaryRepository secretaryRepository;
    private final SecretaryMapper secretaryMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    public SecretaryService(
            SecretaryRepository secretaryRepository,
            SecretaryMapper secretaryMapper,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository
    ) {
        this.secretaryRepository = secretaryRepository;
        this.secretaryMapper = secretaryMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
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

        Secretary savedSecretary = secretaryRepository.save(secretary);

        ActivityLog secretaryLog = new ActivityLog();
        secretaryLog.setAction("Secretary account with ID: " + savedSecretary.getUserId() + "created." ) ;
        secretaryLog.setEntityType("Secretary");
        secretaryLog.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(secretaryLog);

        return secretaryMapper.toSecretaryDTO(savedSecretary);
    }

}
