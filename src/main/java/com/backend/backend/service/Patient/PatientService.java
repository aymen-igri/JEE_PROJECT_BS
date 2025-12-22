package com.backend.backend.service.Patient;

import com.backend.backend.dto.request.Patient.CreatePatientRequest;
import com.backend.backend.dto.response.Patient.CreatePatientResponse;
import com.backend.backend.dto.response.Patient.PatientResponse;
import com.backend.backend.entity.patient.MedicalRecord;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.mapper.Patient.PatientsMapper;
import com.backend.backend.repository.Patient.MedicalRecordRepository;
import com.backend.backend.repository.Patient.PatientRepository;
import com.backend.backend.repository.user.SecretaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class PatientService {

    public final PatientsMapper patientMapper;
    public final PatientRepository patientRepository;
    public final MedicalRecordRepository medicalRecordRepository;
    public final SecretaryRepository secretaryRepository;

    public PatientService(
            PatientsMapper patientMapper,
            PatientRepository patientRepository,
            MedicalRecordRepository medicalRecordRepository,
            SecretaryRepository secretaryRepository
    ) {
        this.patientMapper = patientMapper;
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.secretaryRepository = secretaryRepository;
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request){

        if (patientRepository.existsByCin(request.CIN())){
            throw new IllegalArgumentException("CIN already exists");
        }

        Patient patient = patientMapper.toPatient(request);

        MedicalRecord medicalRecord = new MedicalRecord();

        medicalRecord.setPatient(patient);
        medicalRecord.setAllergies(new ArrayList<>());
        medicalRecord.setBloodType("");
        medicalRecord.setFamilyHistory("");
        medicalRecord.setChronicDiseases("");
        medicalRecord.setPastSurgeries(new ArrayList<>());

        return patientMapper.toPatientDTO(
                patientRepository.save(patient),
                medicalRecordRepository.save(medicalRecord)
        );
    }
}
