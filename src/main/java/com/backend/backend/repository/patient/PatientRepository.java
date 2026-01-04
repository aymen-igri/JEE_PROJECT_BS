package com.backend.backend.repository.patient;

import com.backend.backend.entity.patient.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID>, JpaSpecificationExecutor<Patient> {

    Patient findPatientByPatientId(UUID patientId);
    boolean existsByCin(String cin);

}