package com.backend.backend.service.Doctor;

import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.request.Doctor.DoctorInfoResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.user.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final CabinetRepository cabinetRepository;

    public DoctorService(DoctorRepository doctorRepository, CabinetRepository cabinetRepository) {
        this.doctorRepository = doctorRepository;
        this.cabinetRepository = cabinetRepository;
    }

    public DoctorInfoResponse getDoctorByUsername(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return new DoctorInfoResponse(
                doctor.getFullName(),
                doctor.getEmail(),
                doctor.getUsername(),
                doctor.getCIN(),
                doctor.getPhone(),
                doctor.getAddress(),
                doctor.getDateOfBirth(),
                doctor.getGender(),
                doctor.getSpecialty(),
                doctor.getLicenseNumber(),
                doctor.getStatus(),
                doctor.getProfilePhoto(),
                doctor.getCreatedAt()
        );
    }

    public DoctorInfoResponse updateDoctor(String username, DoctorAppDataRequest request) {
        Doctor doctor = doctorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setCIN(request.CIN());
        doctor.setPhone(request.phone());
        doctor.setSpecialty(request.specialty());
        doctor.setLicenseNumber(request.licenseNumber());
        Doctor updated = doctorRepository.save(doctor);

        return new DoctorInfoResponse(
                doctor.getFullName(),
                doctor.getEmail(),
                doctor.getUsername(),
                doctor.getCIN(),
                doctor.getPhone(),
                doctor.getAddress(),
                doctor.getDateOfBirth(),
                doctor.getGender(),
                doctor.getSpecialty(),
                doctor.getLicenseNumber(),
                doctor.getStatus(),
                doctor.getProfilePhoto(),
                doctor.getCreatedAt()
        );
    }

    public boolean hasCabinet(UUID doctorId) {
        return cabinetRepository.existsByDoctorUserId(doctorId);

    }
}