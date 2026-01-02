package com.backend.backend.mapper.User;

import com.backend.backend.dto.response.User.UserResponce;
import com.backend.backend.entity.User.User;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserRepository userRepository;

    public UserMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponce toUserResponce(User user) {
        return new UserResponce(
                user.getUserId(),
                user.getFullName(),
                user.getCIN(),
                user.getDateOfBirth(),
                user.getCreatedAt(),
                user.getGender(),
                user.getAddress(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getProfilePhoto()
        );
    }
}
