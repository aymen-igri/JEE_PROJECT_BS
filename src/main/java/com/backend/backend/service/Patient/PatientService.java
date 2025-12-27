package com.backend.backend.service.Patient;

import com.backend.backend.dto.request.Patient.CreatePatientRequest;
import com.backend.backend.dto.request.Patient.DoctorPatientLinkRequest;
import com.backend.backend.dto.response.Patient.CreatePatientResponse;
import com.backend.backend.dto.response.Patient.DoctorPatientLinkResponse;
import com.backend.backend.dto.response.Patient.PatientResponse;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.patient.DoctorPatientLink;
import com.backend.backend.entity.patient.MedicalRecord;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.mapper.Patient.PatientsMapper;
import com.backend.backend.repository.Patient.DoctorPatientLinkRepository;
import com.backend.backend.repository.Patient.MedicalRecordRepository;
import com.backend.backend.repository.Patient.PatientRepository;
import com.backend.backend.repository.activity.ActivityLogRepository;
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
    public final DoctorPatientLinkRepository doctorPatientLinkRepository;
    public final ActivityLogRepository activityLogRepository;

    public PatientService(
            PatientsMapper patientMapper,
            PatientRepository patientRepository,
            MedicalRecordRepository medicalRecordRepository,
            SecretaryRepository secretaryRepository,
            DoctorPatientLinkRepository doctorPatientLinkRepository,
            ActivityLogRepository activityLogRepository
    ) {
        this.patientMapper = patientMapper;
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.secretaryRepository = secretaryRepository;
        this.doctorPatientLinkRepository = doctorPatientLinkRepository;
        this.activityLogRepository = activityLogRepository;
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

        ActivityLog patinetLog = new ActivityLog();
        patinetLog.setUser(secretaryRepository.findByUserId(request.registedBY()));
        patinetLog.setAction("Created patient with CIN: " + request.CIN());
        patinetLog.setEntityType("Patient");
        patinetLog.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(patinetLog);

        return patientMapper.toPatientDTO(
                patientRepository.save(patient),
                medicalRecordRepository.save(medicalRecord)
        );
    }

    @Transactional
    public DoctorPatientLinkResponse linkPatient(DoctorPatientLinkRequest request){

        DoctorPatientLink savedLink = patientMapper.toDoctorPatientLink(request);
        savedLink.setLinkedDate(LocalDate.now());

        ActivityLog patinetLog = new ActivityLog();
        patinetLog.setUser(secretaryRepository.findByUserId(request.secretaryId()));
        patinetLog.setAction("Patient with ID: " + request.patientId() + " linked to Doctor with ID: " + request.doctorId());
        patinetLog.setEntityType("DoctorPatientLink");
        patinetLog.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(patinetLog);

        return patientMapper.toDPLinkDTO(
                doctorPatientLinkRepository.save(savedLink)
        );
    }
}
