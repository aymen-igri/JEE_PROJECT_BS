package com.backend.backend.service.Admin;

import com.backend.backend.dto.request.Admin.AdminSignupRequest;
import com.backend.backend.dto.request.Admin.AdminURequest;
import com.backend.backend.dto.request.SuperAdmin.SuperAdminRequest;
import com.backend.backend.dto.response.Admin.AdminResponse;
import com.backend.backend.dto.response.SuperAdmin.SuperAdminResponse;
import com.backend.backend.entity.User.Admin;
import com.backend.backend.entity.User.SuperAdmin;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.mapper.Admin.AdminMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.user.AdminRepository;
import com.backend.backend.repository.user.SuperAdminRepository;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.UUID;

@Service
public class AdminService {

    private final SuperAdminRepository superAdminRepository;
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
            ActivityLogRepository activityLogRepository,
            SuperAdminRepository superAdminRepository
    ) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.superAdminRepository = superAdminRepository;
    }

    @Transactional
    public List<AdminResponse> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream()
                .map(adminMapper::toAdminDTO)
                .toList();
    }

    @Transactional
    public AdminResponse updateAdminProfile(AdminURequest request, UUID superAdminId) {

        Admin admin = adminMapper.toAUpdate(request, superAdminId);
        Admin savedSuperAdmin = adminRepository.save(admin);
        return adminMapper.toAdminDTO(savedSuperAdmin);
    }

    @Transactional
    public AdminResponse createAccount(
            AdminSignupRequest adminRequest,
            UUID superAdminId
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

        SuperAdmin superAdmin = superAdminRepository.findByUserId(superAdminId);

        Admin admin = adminMapper.toAdmin(adminRequest.AdminInfo(), superAdminId);

        admin.setUsername(adminRequest.credentials().username());
        admin.setPassword("{bcrypt}" + passwordEncoder.encode(adminRequest.credentials().password()));
        admin.setRegisteredBySuperAdmin(superAdmin);

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
