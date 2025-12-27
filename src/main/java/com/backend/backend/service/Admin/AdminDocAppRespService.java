package com.backend.backend.service.Admin;

import com.backend.backend.dto.request.Admin.AdminAccResp;
import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.practice.DoctorApplication;
import com.backend.backend.enums.ApplicationStatus;
import com.backend.backend.mapper.Doctor.DoctorAppMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import com.backend.backend.repository.user.AdminRepository;
import com.backend.backend.repository.user.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminDocAppRespService {

    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    private final DoctorApplicationRepository doctorAppRepository;
    private final DoctorAppMapper doctorAppMapper;
    private final ActivityLogRepository activityLogRepository;

    public AdminDocAppRespService(
            DoctorRepository doctorRepository,
            DoctorApplicationRepository doctorAppRepository,
            DoctorAppMapper doctorAppMapper,
            AdminRepository adminRepository,
            ActivityLogRepository activityLogRepository
    ) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.doctorAppRepository = doctorAppRepository;
        this.doctorAppMapper = doctorAppMapper;
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public DoctorAppResponce changeStatus(UUID applicationId, AdminAccResp adminResponse){

        DoctorApplication application = doctorAppRepository.findDoctorApplicationByApplicationId(applicationId);

        if (application == null) {
            throw new IllegalArgumentException("Application not found for ID: " + applicationId);
        }else if (application.getStatus() != ApplicationStatus.PENDING){
            throw new IllegalArgumentException("Can't change the status, the application is " + application.getStatus());
        }

        DoctorApplication updatedApp = doctorAppMapper.toUpdatedApplication(application,adminResponse);
        doctorAppRepository.save(updatedApp);

        ActivityLog appLog = new ActivityLog();
        appLog.setUser(adminRepository.findAdminByUserId(adminResponse.processedBy()));
        appLog.setAction("Changed doctor application status to " + adminResponse.status());
        appLog.setEntityType("DoctorApplication");
        appLog.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(appLog);

        // create doctor account if application is approved
        if (updatedApp.getStatus() == ApplicationStatus.APPROVED){
            Doctor doctorAcc = doctorAppMapper.toDoctor(updatedApp, adminRepository.findAdminByUserId(adminResponse.processedBy()));
            doctorRepository.save(doctorAcc);
            ActivityLog doctorLog = new ActivityLog();
            doctorLog.setUser(adminRepository.findAdminByUserId(adminResponse.processedBy()));
            doctorLog.setAction("Doctor with ID: " + doctorAcc.getUserId() + " account created.");
            doctorLog.setEntityType("Doctor");
            doctorLog.setTimestamp(LocalDateTime.now());
            activityLogRepository.save(doctorLog);
        }

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
