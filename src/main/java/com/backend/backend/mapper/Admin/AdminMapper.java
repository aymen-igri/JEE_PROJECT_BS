package com.backend.backend.mapper.Admin;

import com.backend.backend.dto.request.Admin.AdminRequest;
import com.backend.backend.dto.response.Admin.AdminResponse;
import com.backend.backend.entity.User.Admin;
import com.backend.backend.repository.user.AdminRepository;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    private final AdminRepository adminRepository;

    public AdminMapper(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
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
                admin.getProfilePhoto()
        );
    }
}
