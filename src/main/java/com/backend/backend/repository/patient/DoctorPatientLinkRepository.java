package com.backend.backend.repository.patient;

import com.backend.backend.entity.patient.DoctorPatientLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface DoctorPatientLinkRepository extends JpaRepository<DoctorPatientLink, UUID>, JpaSpecificationExecutor<DoctorPatientLink> {
}
