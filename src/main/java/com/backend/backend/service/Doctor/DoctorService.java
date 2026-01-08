package com.backend.backend.service.Doctor;

import com.backend.backend.dto.request.Doctor.DoctorAppDataRequest;
import com.backend.backend.dto.request.Doctor.DoctorInfoResponse;
import com.backend.backend.dto.response.Doctor.DoctorResponce;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.mapper.Doctor.DoctorMapper;
import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.user.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final CabinetRepository cabinetRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(
            DoctorRepository doctorRepository,
            CabinetRepository cabinetRepository,
            DoctorMapper doctorMapper
    ){
        this.doctorRepository = doctorRepository;
        this.cabinetRepository = cabinetRepository;
        this.doctorMapper = doctorMapper;
    }

    public List<DoctorResponce> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream().map(doctorMapper::toDoctorDTO).toList();
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

    // Statistics methods for dashboard
    public long getTotalDoctorsCount() {
        return doctorRepository.count();
    }

    public long getActiveDoctorsCount() {
        return doctorRepository.countByStatus(com.backend.backend.enums.EStatus.ACTIVE);
    }

    public long getInactiveDoctorsCount() {
        return doctorRepository.countByStatus(com.backend.backend.enums.EStatus.INACTIVE);
    }

    public double getInactiveDoctorsPercentage() {
        long total = getTotalDoctorsCount();
        if (total == 0) return 0.0;
        return (getInactiveDoctorsCount() * 100.0) / total;
    }
}