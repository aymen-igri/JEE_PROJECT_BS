package com.backend.backend.repository.patient;

import com.backend.backend.entity.patient.DoctorPatientLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorPatientLinkRepository extends JpaRepository<DoctorPatientLink, UUID>, JpaSpecificationExecutor<DoctorPatientLink> {
}