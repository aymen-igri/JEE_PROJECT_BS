package com.backend.backend.mapper.Consultation;

import com.backend.backend.dto.request.Consultation.CreateConsultationRequest;
import com.backend.backend.dto.request.Consultation.UpdateConsultationRequest;
import com.backend.backend.dto.response.Consultation.ConsultationDetailResponse;
import com.backend.backend.dto.response.Consultation.ConsultationResponse;
import com.backend.backend.dto.response.Consultation.DiagnosticResponse;
import com.backend.backend.dto.response.Consultation.PrescriptionResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.enums.ConsultationStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for Consultation entity.
 * Stateless, null-safe transformations between entities and DTOs.
 * Consultations are created via patient ID and have no date.
 */
@Component
public class ConsultationMapper {

    /**
     * Maps CreateConsultationRequest to Consultation entity.
     * Sets default status to IN_PROGRESS.
     * Consultations have no date - they are tracked only by status.
     *
     * @param request The creation request DTO
     * @param doctor The authenticated doctor
     * @param patient The patient for this consultation
     * @return New Consultation entity (not persisted)
     */
    public Consultation toEntity(
            CreateConsultationRequest request,
            Doctor doctor,
            Patient patient
    ) {
        Consultation consultation = new Consultation();
        
        consultation.setDoctor(doctor);
        consultation.setPatient(patient);
        consultation.setConsultationType(request.consultationType());
        consultation.setChiefComplaint(request.chiefComplaint());
        consultation.setSymptoms(request.symptoms());
        consultation.setVitalSigns(request.vitalSigns());
        consultation.setPhysicalExam(request.physicalExam());
        consultation.setNotes(request.notes());
        consultation.setStatus(ConsultationStatus.IN_PROGRESS);

        return consultation;
    }

    /**
     * Updates existing Consultation entity with values from UpdateConsultationRequest.
     * Only non-null fields are updated.
     *
     * @param request The update request DTO
     * @param consultation The existing consultation entity to update
     */
    public void updateEntity(UpdateConsultationRequest request, Consultation consultation) {
        if (request.chiefComplaint() != null) {
            consultation.setChiefComplaint(request.chiefComplaint());
        }
        if (request.symptoms() != null) {
            consultation.setSymptoms(request.symptoms());
        }
        if (request.vitalSigns() != null) {
            consultation.setVitalSigns(request.vitalSigns());
        }
        if (request.physicalExam() != null) {
            consultation.setPhysicalExam(request.physicalExam());
        }
        if (request.notes() != null) {
            consultation.setNotes(request.notes());
        }
        if (request.status() != null) {
            consultation.setStatus(request.status());
        }
    }

    /**
     * Maps Consultation entity to ConsultationResponse DTO.
     * Denormalizes patient and doctor names for display.
     *
     * @param consultation The consultation entity (with relationships loaded)
     * @return ConsultationResponse DTO
     */
    public ConsultationResponse toResponse(Consultation consultation) {
        Patient patient = consultation.getPatient();
        Doctor doctor = consultation.getDoctor();

        return new ConsultationResponse(
                consultation.getConsultationId(),
                patient != null ? patient.getPatientId() : null,
                patient != null ? patient.getFirstName() + " " + patient.getLastName() : null,
                doctor != null ? doctor.getUserId() : null,
                doctor != null ? doctor.getFullName() : null,
                consultation.getConsultationType(),
                consultation.getChiefComplaint(),
                consultation.getSymptoms(),
                consultation.getVitalSigns(),
                consultation.getPhysicalExam(),
                consultation.getNotes(),
                consultation.getStatus(),
                consultation.getCreatedAt(),
                consultation.getUpdatedAt()
        );
    }

    /**
     * Maps Consultation entity to ConsultationDetailResponse DTO.
     * Includes nested diagnostics and prescriptions.
     *
     * @param consultation The consultation entity
     * @param diagnostics List of diagnostic responses
     * @param prescriptions List of prescription responses
     * @return ConsultationDetailResponse DTO
     */
    public ConsultationDetailResponse toDetailResponse(
            Consultation consultation,
            List<DiagnosticResponse> diagnostics,
            List<PrescriptionResponse> prescriptions
    ) {
        Patient patient = consultation.getPatient();
        Doctor doctor = consultation.getDoctor();

        return new ConsultationDetailResponse(
                consultation.getConsultationId(),
                patient != null ? patient.getPatientId() : null,
                patient != null ? patient.getFirstName() + " " + patient.getLastName() : null,
                doctor != null ? doctor.getUserId() : null,
                doctor != null ? doctor.getFullName() : null,
                consultation.getConsultationType(),
                consultation.getChiefComplaint(),
                consultation.getSymptoms(),
                consultation.getVitalSigns(),
                consultation.getPhysicalExam(),
                consultation.getNotes(),
                consultation.getStatus(),
                diagnostics,
                prescriptions,
                consultation.getCreatedAt(),
                consultation.getUpdatedAt()
        );
    }
}

