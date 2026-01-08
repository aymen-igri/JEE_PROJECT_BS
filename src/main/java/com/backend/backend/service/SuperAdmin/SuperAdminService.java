package com.backend.backend.service.SuperAdmin;

import com.backend.backend.dto.request.SuperAdmin.SuperAdminRequest;
import com.backend.backend.dto.response.SuperAdmin.SuperAdminResponse;
import com.backend.backend.entity.User.SuperAdmin;
import com.backend.backend.mapper.SuperAdmin.SuperAdminMapper;
import com.backend.backend.repository.user.SuperAdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final SuperAdminMapper superAdminMapper;

    public SuperAdminService(
            SuperAdminRepository superAdminRepository,
            SuperAdminMapper superAdminMapper
    ) {
        this.superAdminRepository = superAdminRepository;
        this.superAdminMapper = superAdminMapper;
    }

    @Transactional
    public SuperAdmin getMainSuperAdmin(UUID superAdminId) {
        return superAdminRepository.findById(superAdminId).orElseThrow(
                () -> new RuntimeException("SuperAdmin not found")
        );
    }

    @Transactional
    public SuperAdminResponse updateSuperAdminProfile(SuperAdminRequest request, UUID superAdminId) {

        SuperAdmin superAdmin = superAdminMapper.toSPUpdate(request, superAdminId);
        SuperAdmin savedSuperAdmin = superAdminRepository.save(superAdmin);
        return superAdminMapper.toSPDTO(savedSuperAdmin);
    }
}
