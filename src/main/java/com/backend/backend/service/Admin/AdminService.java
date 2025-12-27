package com.backend.backend.service.Admin;

import com.backend.backend.dto.request.Admin.AdminSignupRequest;
import com.backend.backend.dto.response.Admin.AdminResponse;
import com.backend.backend.entity.User.Admin;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.mapper.Admin.AdminMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.user.AdminRepository;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    public AdminService(
            AdminRepository adminRepository,
            AdminMapper adminMapper,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository
    ) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public AdminResponse createAccount(
            AdminSignupRequest adminRequest
    ) {
        if (userRepository.existsByUsername(adminRequest.credentials().username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(adminRequest.AdminInfo().email())){
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByCIN(adminRequest.AdminInfo().CIN())){
            throw new IllegalArgumentException("CIN already exists");
        }

        Admin admin = adminMapper.toAdmin(adminRequest.AdminInfo());

        admin.setUsername(adminRequest.credentials().username());
        admin.setPassword(passwordEncoder.encode(adminRequest.credentials().password()));

        Admin savedAdmin = adminRepository.save(admin);

        ActivityLog adminLog = new ActivityLog();
        adminLog.setUser(adminRepository.findAdminByUserId(savedAdmin.getRegisteredBySuperAdmin().getUserId()));
        adminLog.setAction("Admin account with ID: " + savedAdmin.getUserId() + " created");
        adminLog.setEntityType("Admin");
        adminLog.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(adminLog);

        return adminMapper.toAdminDTO(savedAdmin);
    }

}
