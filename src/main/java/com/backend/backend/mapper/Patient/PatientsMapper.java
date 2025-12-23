package com.backend.backend.mapper.Patient;

import com.backend.backend.dto.request.Patient.CreateMRRequest;
import com.backend.backend.dto.request.Patient.CreatePatientRequest;
import com.backend.backend.dto.request.Patient.DoctorPatientLinkRequest;
import com.backend.backend.dto.response.Patient.CreateMRResponse;
import com.backend.backend.dto.response.Patient.CreatePatientResponse;
import com.backend.backend.dto.response.Patient.DoctorPatientLinkResponse;
import com.backend.backend.dto.response.Patient.PatientResponse;
import com.backend.backend.entity.patient.DoctorPatientLink;
import com.backend.backend.entity.patient.MedicalRecord;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.repository.Patient.PatientRepository;
import com.backend.backend.repository.user.DoctorRepository;
import com.backend.backend.repository.user.SecretaryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PatientsMapper {
    private final DoctorRepository doctorRepository;
    private final SecretaryRepository secretaryRepository;
    private final PatientRepository patientRepository;

    public PatientsMapper(
            DoctorRepository doctorRepository,
            SecretaryRepository secretaryRepository,
            PatientRepository patientRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.secretaryRepository = secretaryRepository;
        this.patientRepository = patientRepository;
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

    public DoctorPatientLink toDoctorPatientLink(DoctorPatientLinkRequest request){

        DoctorPatientLink doctorPatientLink = new DoctorPatientLink();

        doctorPatientLink.setDoctor(doctorRepository.findDoctorByUserId(request.doctorId()));
        doctorPatientLink.setPatient(patientRepository.findPatientByPatientId(request.patientId()));
        doctorPatientLink.setLinkedBySecretary(secretaryRepository.findByUserId(request.secretaryId()));

        return doctorPatientLink;
    }

    public MedicalRecord toMedRecord(CreateMRRequest request){

        MedicalRecord medicalRecord = new MedicalRecord();
        Patient thatPatient = patientRepository.findPatientByPatientId(request.patientID());

        medicalRecord.setPatient(thatPatient);
        medicalRecord.setAllergies(request.allergies());
        medicalRecord.setBloodType(request.bloodType());
        medicalRecord.setChronicDiseases(request.chronicDiseases());
        medicalRecord.setFamilyHistory(request.familyHistory());
        medicalRecord.setPastSurgeries(request.pastSurgeries());

        return medicalRecord;
    }

    public PatientResponse toPatientDTO(Patient newPatient, MedicalRecord newMR){

        CreatePatientResponse patientResponse = new CreatePatientResponse(
                newPatient.getPatientId(),
                newPatient.getFirstName(),
                newPatient.getLastName(),
                newPatient.getCin(),
                newPatient.getDateOfBirth(),
                newPatient.getGender(),
                newPatient.getPhone(),
                newPatient.getAddress(),
                newPatient.getRegistrationDate(),
                newPatient.getRegisteredBySecretary().getUserId()
        );

        CreateMRResponse medicalRecordResponse = new CreateMRResponse(
                newMR.getRecordId(),
                newMR.getPatient().getPatientId(),
                newMR.getAllergies(),
                newMR.getBloodType(),
                newMR.getChronicDiseases(),
                newMR.getFamilyHistory(),
                newMR.getPastSurgeries(),
                newMR.getCreatedAt()
        );

        return new PatientResponse(
                patientResponse,
                medicalRecordResponse
        );
    }

    public DoctorPatientLinkResponse toDPLinkDTO(DoctorPatientLink savedLink){

        return new DoctorPatientLinkResponse(
                savedLink.getLinkId(),
                savedLink.getDoctor().getUserId(),
                savedLink.getPatient().getPatientId(),
                savedLink.getLinkedBySecretary().getUserId(),
                savedLink.getStatus(),
                savedLink.getLinkedDate()
        );
    }


}
