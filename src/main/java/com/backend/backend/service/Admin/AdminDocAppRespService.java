package com.backend.backend.service.Admin;

import com.backend.backend.dto.request.Admin.AdminAccResp;
import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.entity.practice.DoctorApplication;
import com.backend.backend.enums.ApplicationStatus;
import com.backend.backend.mapper.Doctor.DoctorAppMapper;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminDocAppRespService {

    private final DoctorApplicationRepository doctorAppRepository;
    private final DoctorAppMapper doctorAppMapper;

    public AdminDocAppRespService(DoctorApplicationRepository doctorAppRepository, DoctorAppMapper doctorAppMapper) {
        this.doctorAppRepository = doctorAppRepository;
        this.doctorAppMapper = doctorAppMapper;
    }

    public DoctorAppResponce changeStatus(UUID applicationId, AdminAccResp adminResponse){

        DoctorApplication application = doctorAppRepository.findDoctorApplicationByApplicationId(applicationId);

        if (application == null) {
            throw new IllegalArgumentException("Application not found for ID: " + applicationId);
        }else if (application.getStatus() != ApplicationStatus.PENDING){
            throw new IllegalArgumentException("Can't change the status, the application is " + application.getStatus());
        }

        DoctorApplication updatedApp = doctorAppMapper.toUpdatedApplication(application,adminResponse);
        doctorAppRepository.save(application);

        return new DoctorAppResponce(
                application.getApplicationId(),
                application.getFullName(),
                application.getEmail(),
                application.getCin(),
                application.getPhone(),
                application.getSpecialty(),
                application.getLicenseNumber(),
                application.getDiplomaDocument(),
                application.getLicenseDocument(),
                application.getCvDocument(),
                application.getStatus(),
                application.getApplicationDate()
        );
    }
}
