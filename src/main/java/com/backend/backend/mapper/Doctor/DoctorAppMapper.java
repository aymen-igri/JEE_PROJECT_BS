package com.backend.backend.mapper.Doctor;

import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.entity.practice.DoctorApplication;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import org.springframework.stereotype.Component;

@Component
public class DoctorAppMapper {

    public final DoctorApplicationRepository doctorApplicationRepository;

    public DoctorAppMapper(DoctorApplicationRepository doctorApplicationRepository) {
        this.doctorApplicationRepository = doctorApplicationRepository;
    }

    public DoctorApplication toApplication(DoctorAppDataRequest request) {

        DoctorApplication application = new DoctorApplication();

        application.setFullName(request.fullName());
        application.setEmail(request.email());
        application.setCin(request.CIN());
        application.setPhone(request.phone());
        application.setSpecialty(request.specialty());
        application.setLicenseNumber(request.licenseNumber());
        application.setDiplomaDocument(request.diplomaDocument());
        application.setLicenseDocument(request.licenseDocument());
        application.setCvDocument(request.cvDocument());
        application.setStatus(request.status());

        return application;

    }

    public DoctorAppResponce toAppDTO(DoctorApplication application) {

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
