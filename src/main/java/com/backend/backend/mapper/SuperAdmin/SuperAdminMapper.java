package com.backend.backend.mapper.SuperAdmin;

import com.backend.backend.dto.request.SuperAdmin.SuperAdminRequest;
import com.backend.backend.dto.response.SuperAdmin.SuperAdminResponse;
import com.backend.backend.entity.User.SuperAdmin;
import com.backend.backend.repository.user.SuperAdminRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SuperAdminMapper {

    private final SuperAdminRepository superAdminRepository;

    public SuperAdminMapper(SuperAdminRepository superAdminRepository) {
        this.superAdminRepository = superAdminRepository;
    }

    public SuperAdmin toSP(SuperAdminRequest superAdminRequest) {

        SuperAdmin superAdmin = new SuperAdmin();

        superAdmin.setCIN(superAdminRequest.CIN());
        superAdmin.setAddress(superAdminRequest.address());
        superAdmin.setDateOfBirth(superAdminRequest.dateOfBirth());
        superAdmin.setFullName(superAdminRequest.fullName());
        superAdmin.setGender(superAdminRequest.gender());
        superAdmin.setPhone(superAdminRequest.phone());
        return superAdmin;
    }

    public SuperAdmin toSPUpdate(SuperAdminRequest superAdminRequest, UUID superAdminId) {

        SuperAdmin superAdmin = superAdminRepository.findByUserId(superAdminId);

        superAdmin.setCIN(superAdminRequest.CIN());
        superAdmin.setAddress(superAdminRequest.address());
        superAdmin.setDateOfBirth(superAdminRequest.dateOfBirth());
        superAdmin.setFullName(superAdminRequest.fullName());
        superAdmin.setGender(superAdminRequest.gender());
        superAdmin.setPhone(superAdminRequest.phone());
        return superAdmin;
    }

    public SuperAdminResponse toSPDTO(SuperAdmin superAdmin) {
        return new SuperAdminResponse(
                superAdmin.getUserId(),
                superAdmin.getLevel(),
                superAdmin.getCIN(),
                superAdmin.getAddress(),
                superAdmin.getDateOfBirth(),
                superAdmin.getFullName(),
                superAdmin.getGender(),
                superAdmin.getPhone(),
                superAdmin.getStatus(),
                superAdmin.getCreatedAt(),
                superAdmin.getUpdatedAt()
        );
    }
}
