package com.backend.backend.service.Secretary;

import com.backend.backend.dto.request.Auth.AuthRequest;
import com.backend.backend.dto.request.Secretary.SecretaryRequest;
import com.backend.backend.dto.response.Secretary.SecretaryResponse;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.mapper.Secretary.SecretaryMapper;
import com.backend.backend.repository.user.SecretaryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecretaryService {

    private final SecretaryRepository secretaryRepository;
    private final SecretaryMapper secretaryMapper;
    private final PasswordEncoder passwordEncoder;

    public SecretaryService(SecretaryRepository secretaryRepository, SecretaryMapper secretaryMapper, PasswordEncoder passwordEncoder) {
        this.secretaryRepository = secretaryRepository;
        this.secretaryMapper = secretaryMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public SecretaryResponse signUp(SecretaryRequest secretaryRequest, AuthRequest authRequest) {

        Secretary secretary = secretaryMapper.toSecretary(secretaryRequest);

        secretary.setUsername(authRequest.username());
        secretary.setPassword(passwordEncoder.encode(authRequest.password()));

        Secretary savedSecretary = secretaryRepository.save(secretary);

        return secretaryMapper.toSecretaryDTO(savedSecretary);

    }

}
