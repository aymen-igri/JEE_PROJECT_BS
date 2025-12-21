package com.backend.backend.service.Admin;

import com.backend.backend.dto.request.Admin.AdminAccResp;
import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.practice.DoctorApplication;
import com.backend.backend.enums.ApplicationStatus;
import com.backend.backend.mapper.Doctor.DoctorAppMapper;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import com.backend.backend.repository.user.AdminRepository;
import com.backend.backend.repository.user.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminDocAppRespService {

    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final DoctorApplicationRepository doctorAppRepository;
    private final DoctorAppMapper doctorAppMapper;

    public AdminDocAppRespService(DoctorRepository doctorRepository, DoctorApplicationRepository doctorAppRepository, DoctorAppMapper doctorAppMapper, AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
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
        doctorAppRepository.save(updatedApp);

        Doctor doctorAcc = doctorAppMapper.toDoctor(updatedApp, adminRepository.findAdminByUserId(adminResponse.processedBy()));
        doctorRepository.save(doctorAcc);

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
