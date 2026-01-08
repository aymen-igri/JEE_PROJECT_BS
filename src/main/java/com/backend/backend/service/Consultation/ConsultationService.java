package com.backend.backend.service.Consultation;

import com.backend.backend.dto.response.Dashboard.ConsultationCardDTO;
import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.repository.consultation.ConsultationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    @Transactional(readOnly = true)
    public List<ConsultationCardDTO> getLatestConsultationsForDoctor(UUID doctorId) {
        List<Consultation> consultations = consultationRepository
                .findLatestConsultationsWithDetails(doctorId);

        // Limit to 9
        return consultations.stream()
                .limit(9)
                .map(this::mapToConsultationCardDTO)
                .collect(Collectors.toList());
    }

    private ConsultationCardDTO mapToConsultationCardDTO(Consultation consultation) {
        Patient patient = consultation.getRecord() != null ?
                consultation.getRecord().getPatient() : null;

        if (patient == null) {
            // Fallback if patient data is missing
            return new ConsultationCardDTO(
                    consultation.getConsultationId(),
                    "Unknown Patient",
                    "",
                    "",
                    "",
                    consultation.getNotes() != null ? consultation.getNotes() : "",
                    consultation.getStatus() != null ? consultation.getStatus() : "UNKNOWN"
            );
        }

        String patientName = (patient.getFirstName() != null ? patient.getFirstName() : "") +
                " " +
                (patient.getLastName() != null ? patient.getLastName() : "");

        String dateOfBirth = patient.getDateOfBirth() != null ?
                patient.getDateOfBirth().format(DATE_FORMATTER) : "";

        String sex = patient.getGender() != null ? patient.getGender().toString() :  "";

        String phone = patient.getPhone() != null ? patient.getPhone() : "";

        String notes = consultation.getNotes() != null ? consultation.getNotes() : "";

        String status = consultation.getStatus() != null ? consultation.getStatus() : "PENDING";

        return new ConsultationCardDTO(
                consultation.getConsultationId(),
                patientName.trim(),
                dateOfBirth,
                sex,
                phone,
                notes,
                status
        );
    }
}