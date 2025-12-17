package com.backend.backend.mapper.Secretary;

import com.backend.backend.dto.request.Secretary.SecretaryRequest;
import com.backend.backend.dto.response.Secretary.SecretaryResponse;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.repository.user.SecretaryRepository;
import org.springframework.stereotype.Component;

@Component
public class SecretaryMapper {
    private final SecretaryRepository secretaryRepository;

    public SecretaryMapper(SecretaryRepository secretaryRepository) {
        this.secretaryRepository = secretaryRepository;
    }

    public Secretary toSecretary(SecretaryRequest secretaryRequest){

        Secretary secretary = new Secretary();

        secretary.setFullName(secretaryRequest.fullName());
        secretary.setDateOfBirth(secretaryRequest.dateOfBirth());
        secretary.setGender(secretaryRequest.gender());
        secretary.setAddress(secretaryRequest.address());
        secretary.setCIN(secretaryRequest.CIN());
        secretary.setEmail(secretaryRequest.email());
        secretary.setPhone(secretaryRequest.phone());

        return secretary;
    }

    public SecretaryResponse toSecretaryDTO(Secretary secretary){

        return new SecretaryResponse(
                secretary.getUserId(),
                secretary.getFullName(),
                secretary.getCIN(),
                secretary.getDateOfBirth(),
                secretary.getGender(),
                secretary.getAddress(),
                secretary.getEmail(),
                secretary.getPhone(),
                secretary.getProfilePhoto()
        );
    }
}
