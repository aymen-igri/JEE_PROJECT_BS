package com.backend.backend.mapper.Patient;

import com.backend.backend.dto.request.Patient.CreatePatientRequest;
import com.backend.backend.dto.response.Patient.CreatePatientResponse;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.repository.user.SecretaryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PatientsMapper {
    private final SecretaryRepository secretaryRepository;

    public PatientsMapper(SecretaryRepository secretaryRepository) {
        this.secretaryRepository = secretaryRepository;
    }

    public Patient toPatient(CreatePatientRequest request){

        Patient patient = new Patient();

        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setCin(request.CIN());
        patient.setAddress(request.address());
        patient.setGender(request.gender());
        patient.setPhone(request.phone());
        patient.setRegistrationDate(LocalDate.now());
        patient.setRegisteredBySecretary(secretaryRepository.findByUserId(request.registedBY()));

        return patient;
    }

    public CreatePatientResponse toPatientDTO(Patient patient){

        return new CreatePatientResponse(
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getCin(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getRegistrationDate(),
                patient.getRegisteredBySecretary().getUserId()
        );
    }
}
