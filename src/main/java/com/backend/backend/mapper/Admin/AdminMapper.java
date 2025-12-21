package com.backend.backend.mapper.Admin;

import com.backend.backend.dto.request.Admin.AdminRequest;
import com.backend.backend.dto.response.Admin.AdminResponse;
import com.backend.backend.entity.User.Admin;
import com.backend.backend.entity.User.SuperAdmin;
import com.backend.backend.repository.user.AdminRepository;
import com.backend.backend.repository.user.SuperAdminRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminMapper {

    private final AdminRepository adminRepository;
    private final SuperAdminRepository superAdminRepository;

    public AdminMapper(AdminRepository adminRepository, SuperAdminRepository superAdminRepository) {
        this.adminRepository = adminRepository;
        this.superAdminRepository = superAdminRepository;
    }

    public Admin toAdmin(AdminRequest adminRequest){

        Admin admin = new Admin();

        admin.setFullName(adminRequest.fullName());
        admin.setCIN(adminRequest.CIN());
        admin.setDateOfBirth(adminRequest.dateOfBirth());
        admin.setGender(adminRequest.gender());
        admin.setAddress(adminRequest.address());
        admin.setEmail(adminRequest.email());
        admin.setPhone(adminRequest.phone());
        admin.setRegisteredBySuperAdmin(superAdminRepository.findByUserId(adminRequest.registeredBy()));

        return admin;
    }

    public AdminResponse toAdminDTO(Admin admin){

        return new AdminResponse(
                admin.getUserId(),
                admin.getFullName(),
                admin.getCIN(),
                admin.getDateOfBirth(),
                admin.getGender(),
                admin.getAddress(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getCreatedAt(),
                admin.getProfilePhoto()
        );
    }
}
