package com.backend.backend.mapper.Doctor;

import com.backend.backend.dto.response.Doctor.DoctorAppResponce;
import com.backend.backend.dto.response.Doctor.DoctorResponce;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.repository.user.DoctorRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorMapper {

    private final DoctorRepository doctorRepository;

    public DoctorMapper(
            DoctorRepository doctorRepository
    ) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponce toDoctorDTO(Doctor doctor) {
        return new DoctorResponce(
                doctor.getUserId(),
                doctor.getFullName(),
                doctor.getCIN(),
                doctor.getDateOfBirth(),
                doctor.getCreatedAt(),
                doctor.getGender(),
                doctor.getEmail(),
                doctor.getAddress(),
                doctor.getPhone(),
                doctor.getSpecialty(),
                doctor.getLicenseNumber(),
                doctor.getProfilePhoto(),
                doctor.getStatus()
        );
    }
}
