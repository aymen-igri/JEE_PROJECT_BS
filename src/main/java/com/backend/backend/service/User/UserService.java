package com.backend.backend.service.User;

import com.backend.backend.dto.request.User.VerifDuplRequest;
import com.backend.backend.dto.response.User.UserResponce;
import com.backend.backend.enums.EStatus;
import com.backend.backend.mapper.User.UserMapper;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DoctorApplicationRepository doctorApplicationRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            DoctorApplicationRepository doctorApplicationRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.doctorApplicationRepository = doctorApplicationRepository;
        this.userMapper = userMapper;
    }

    public boolean userExists(VerifDuplRequest userReq){
        if(!userRepository.existsByUsername(userReq.username())
                && !userRepository.existsByEmail(userReq.email())
                && !doctorApplicationRepository.existsByEmail(userReq.email())
                && !doctorApplicationRepository.existsByUsername(userReq.username())){
            return true;
        }else{
            if(userRepository.existsByUsername(userReq.username())
                    || doctorApplicationRepository.existsByUsername(userReq.username())){
                throw new IllegalArgumentException("Email already taken");

            }
            if (userRepository.existsByEmail(userReq.email())
                    || doctorApplicationRepository.existsByEmail(userReq.email())){
                throw new IllegalArgumentException("Email already taken");
            }
            return false;
        }
    }

    public List<UserResponce> getAllUsers(){
        return userRepository.findAll().stream().map(userMapper::toUserResponce).toList();
    }

    public UserResponce suspendUser(UUID id){
        var user = userRepository.findUserByUserId(id)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
        user.setStatus(EStatus.SUSPENDED);
        var savedUser = userRepository.save(user);
        return userMapper.toUserResponce(savedUser);
    }
}
