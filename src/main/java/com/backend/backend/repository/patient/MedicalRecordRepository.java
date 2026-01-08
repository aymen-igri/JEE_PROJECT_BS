package com.backend.backend.repository.Patient;

import com.backend.backend.entity.patient.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID>, JpaSpecificationExecutor<MedicalRecord> {

    /**
     * Find medical record by patient ID.
     */
    Optional<MedicalRecord> findByPatientPatientId(UUID patientId);
}