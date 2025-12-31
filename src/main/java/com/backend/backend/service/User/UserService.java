package com.backend.backend.service.User;

import com.backend.backend.dto.request.User.VerifDuplRequest;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(VerifDuplRequest userReq){
        if(!userRepository.existsByUsername(userReq.username()) && !userRepository.existsByEmail(userReq.email())){
            return true;
        }else{
            if(userRepository.existsByUsername(userReq.username())){
                throw new IllegalArgumentException("Username already taken");

            }
            if (userRepository.existsByEmail(userReq.email())){
                throw new IllegalArgumentException("Email already taken");
            }
            return false;
        }
    }
}
