package com.backend.backend.service.Patient;

import com.backend.backend.dto.request.Patient.CreatePatientRequest;
import com.backend.backend.dto.response.Patient.CreatePatientResponse;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.mapper.Patient.PatientsMapper;
import com.backend.backend.repository.Patient.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    public final PatientsMapper patientMapper;
    public final PatientRepository patientRepository;

    public PatientService(PatientsMapper patientMapper, PatientRepository patientRepository) {
        this.patientMapper = patientMapper;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public CreatePatientResponse createPatient(
            CreatePatientRequest request
    ){
        if (patientRepository.existsByCin(request.CIN())){
            throw new IllegalArgumentException("CIN already exists");
        }

        Patient patient = patientMapper.toPatient(request);
        patient = patientRepository.save(patient);

        CreatePatientResponse savedPatient = patientMapper.toPatientDTO(patient);

        return savedPatient;
    }
}
