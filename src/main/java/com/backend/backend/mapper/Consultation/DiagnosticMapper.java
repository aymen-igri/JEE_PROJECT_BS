package com.backend.backend.mapper.Consultation;

import com.backend.backend.dto.request.Consultation.CreateDiagnosticRequest;
import com.backend.backend.dto.request.Consultation.UpdateDiagnosticRequest;
import com.backend.backend.dto.response.Consultation.DiagnosticResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.entity.patient.Diagnostic;
import com.backend.backend.entity.patient.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Diagnostic entity.
 * Stateless, null-safe transformations between entities and DTOs.
 * Each diagnostic has its own date since consultations can span multiple appointments.
 * Supports grace period computation for canModify flag.
 */
@Component
public class DiagnosticMapper {

    /**
     * Maps CreateDiagnosticRequest to Diagnostic entity.
     * Sets diagnosisDate to current date if not provided.
     *
     * @param request The creation request DTO
     * @param consultation The consultation this diagnostic belongs to
     * @return New Diagnostic entity (not persisted)
     */
    public Diagnostic toEntity(CreateDiagnosticRequest request, Consultation consultation) {
        Diagnostic diagnostic = new Diagnostic();
        
        diagnostic.setConsultation(consultation);
        diagnostic.setDiagnosisDate(request.diagnosisDate() != null ? request.diagnosisDate() : LocalDate.now());
        diagnostic.setDiagnosis(request.diagnosis());
        diagnostic.setDiagnosisCode(request.diagnosisCode());
        diagnostic.setSeverity(request.severity());
        diagnostic.setNotes(request.notes());
        
        return diagnostic;
    }

    /**
     * Updates existing Diagnostic entity with values from UpdateDiagnosticRequest.
     * All fields are replaced (not partial update for diagnostics).
     *
     * @param request The update request DTO
     * @param diagnostic The existing diagnostic entity to update
     */
    public void updateEntity(UpdateDiagnosticRequest request, Diagnostic diagnostic) {
        diagnostic.setDiagnosis(request.diagnosis());
        diagnostic.setDiagnosisCode(request.diagnosisCode());
        diagnostic.setSeverity(request.severity());
        diagnostic.setNotes(request.notes());
    }

    /**
     * Maps Diagnostic entity to DiagnosticResponse DTO.
     * Computes canModify flag based on grace period.
     *
     * @param diagnostic The diagnostic entity (with relationships loaded)
     * @param canModify Whether the diagnostic can still be modified (within grace period)
     * @return DiagnosticResponse DTO
     */
    public DiagnosticResponse toResponse(Diagnostic diagnostic, boolean canModify) {
        Consultation consultation = diagnostic.getConsultation();
        Patient patient = consultation != null ? consultation.getPatient() : null;
        Doctor doctor = consultation != null ? consultation.getDoctor() : null;

        return new DiagnosticResponse(
                diagnostic.getDiagnosisId(),
                consultation != null ? consultation.getConsultationId() : null,
                patient != null ? patient.getPatientId() : null,
                patient != null ? patient.getFirstName() + " " + patient.getLastName() : null,
                doctor != null ? doctor.getUserId() : null,
                doctor != null ? doctor.getFullName() : null,
                diagnostic.getDiagnosisDate(),
                diagnostic.getDiagnosis(),
                diagnostic.getDiagnosisCode(),
                diagnostic.getSeverity(),
                diagnostic.getNotes(),
                diagnostic.getCreatedAt(),
                diagnostic.getUpdatedAt(),
                canModify
        );
    }

    /**
     * Maps a list of Diagnostic entities to DiagnosticResponse DTOs.
     * Computes canModify flag for each based on grace period.
     *
     * @param diagnostics List of diagnostic entities
     * @param gracePeriodMinutes Grace period in minutes from creation
     * @return List of DiagnosticResponse DTOs
     */
    public List<DiagnosticResponse> toResponseList(List<Diagnostic> diagnostics, int gracePeriodMinutes) {
        LocalDateTime now = LocalDateTime.now();
        
        return diagnostics.stream()
                .map(diagnostic -> {
                    boolean canModify = isWithinGracePeriod(diagnostic.getCreatedAt(), gracePeriodMinutes, now);
                    return toResponse(diagnostic, canModify);
                })
                .collect(Collectors.toList());
    }

    /**
     * Checks if a diagnostic is within the grace period for modification.
     *
     * @param createdAt The creation timestamp
     * @param gracePeriodMinutes Grace period in minutes
     * @param now Current time
     * @return true if within grace period
     */
    private boolean isWithinGracePeriod(LocalDateTime createdAt, int gracePeriodMinutes, LocalDateTime now) {
        if (createdAt == null) {
            return false;
        }
        LocalDateTime cutoff = createdAt.plusMinutes(gracePeriodMinutes);
        return now.isBefore(cutoff);
    }
}

