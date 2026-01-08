package com.backend.backend.service.Consultation;

import com.backend.backend.dto.request.Consultation.CreateDiagnosticRequest;
import com.backend.backend.dto.request.Consultation.UpdateDiagnosticRequest;
import com.backend.backend.dto.response.Consultation.DiagnosticResponse;
import com.backend.backend.entity.User.User;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.entity.patient.Diagnostic;
import com.backend.backend.enums.ConsultationStatus;
import com.backend.backend.exception.GracePeriodExpiredException;
import com.backend.backend.mapper.Consultation.DiagnosticMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.consultation.ConsultationRepository;
import com.backend.backend.repository.consultation.DiagnosticRepository;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for Diagnostic management.
 * Handles creation and update of diagnostics with security validation.
 * Enforces grace period for modifications.
 * All operations are logged for audit trail.
 */
@Service
public class DiagnosticService {

    private final DiagnosticRepository diagnosticRepository;
    private final ConsultationRepository consultationRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final DiagnosticMapper diagnosticMapper;

    @Value("${app.diagnostic.grace-period-minutes:30}")
    private int gracePeriodMinutes;

    public DiagnosticService(
            DiagnosticRepository diagnosticRepository,
            ConsultationRepository consultationRepository,
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository,
            DiagnosticMapper diagnosticMapper
    ) {
        this.diagnosticRepository = diagnosticRepository;
        this.consultationRepository = consultationRepository;
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.diagnosticMapper = diagnosticMapper;
    }

    /**
     * Creates a new diagnostic for a consultation.
     * Constraint: Can only add diagnostics to IN_PROGRESS consultations.
     */
    @Transactional
    public DiagnosticResponse createDiagnostic(UUID doctorId, CreateDiagnosticRequest request) {
        Consultation consultation = consultationRepository.findByIdAndDoctorId(request.consultationId(), doctorId)
                .orElseThrow(() -> new AccessDeniedException("Consultation not found or you don't have access"));

        // Validate consultation status - must be IN_PROGRESS to add diagnostics
        if (consultation.getStatus() != ConsultationStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Cannot add diagnostics to consultation with status: " + consultation.getStatus() +
                    ". Consultation must be IN_PROGRESS.");
        }

        Diagnostic diagnostic = diagnosticMapper.toEntity(request, consultation);
        Diagnostic saved = diagnosticRepository.save(diagnostic);

        logActivity("DIAGNOSTIC_CREATED", "Diagnostic", saved.getDiagnosisId(), doctorId,
                "Created diagnostic for consultation " + consultation.getConsultationId());

        return diagnosticMapper.toResponse(saved, true);
    }

    /**
     * Updates an existing diagnostic. Only allowed within the grace period.
     */
    @Transactional
    public DiagnosticResponse updateDiagnostic(UUID doctorId, UUID diagnosticId, UpdateDiagnosticRequest request) {
        Diagnostic diagnostic = diagnosticRepository.findByIdAndDoctorId(diagnosticId, doctorId)
                .orElseThrow(() -> new AccessDeniedException("Diagnostic not found or you don't have access"));

        if (!isWithinGracePeriod(diagnostic)) {
            throw new GracePeriodExpiredException("diagnostic", gracePeriodMinutes);
        }

        String oldDiagnosis = diagnostic.getDiagnosis();
        diagnosticMapper.updateEntity(request, diagnostic);
        Diagnostic saved = diagnosticRepository.save(diagnostic);

        logActivity("DIAGNOSTIC_UPDATED", "Diagnostic", saved.getDiagnosisId(), doctorId,
                "Updated diagnostic. Previous: [" + oldDiagnosis.substring(0, Math.min(50, oldDiagnosis.length())) + "...]");

        return diagnosticMapper.toResponse(saved, isWithinGracePeriod(saved));
    }

    /**
     * Gets a diagnostic by ID with ownership validation.
     */
    @Transactional(readOnly = true)
    public DiagnosticResponse getDiagnosticById(UUID doctorId, UUID diagnosticId) {
        Diagnostic diagnostic = diagnosticRepository.findByIdAndDoctorId(diagnosticId, doctorId)
                .orElseThrow(() -> new AccessDeniedException("Diagnostic not found or you don't have access"));

        return diagnosticMapper.toResponse(diagnostic, isWithinGracePeriod(diagnostic));
    }

    /**
     * Gets all diagnostics for a consultation.
     */
    @Transactional(readOnly = true)
    public List<DiagnosticResponse> getDiagnosticsByConsultation(UUID doctorId, UUID consultationId) {
        if (!consultationRepository.existsByConsultationIdAndDoctorUserId(consultationId, doctorId)) {
            throw new AccessDeniedException("Consultation not found or you don't have access");
        }

        List<Diagnostic> diagnostics = diagnosticRepository.findByConsultationConsultationId(consultationId);
        return diagnosticMapper.toResponseList(diagnostics, gracePeriodMinutes);
    }

    /**
     * Gets paginated list of diagnostics for a patient.
     */
    @Transactional(readOnly = true)
    public Page<DiagnosticResponse> getDiagnosticsByPatient(UUID doctorId, UUID patientId, Pageable pageable) {
        return diagnosticRepository.findByPatientIdPaged(patientId, pageable)
                .map(diagnostic -> diagnosticMapper.toResponse(diagnostic, isWithinGracePeriod(diagnostic)));
    }

    /**
     * Checks if a diagnostic can still be modified (within grace period).
     */
    public boolean canModifyDiagnostic(UUID diagnosticId) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(gracePeriodMinutes);
        return diagnosticRepository.isWithinGracePeriod(diagnosticId, cutoff);
    }

    private boolean isWithinGracePeriod(Diagnostic diagnostic) {
        if (diagnostic.getCreatedAt() == null) {
            return false;
        }
        LocalDateTime cutoff = diagnostic.getCreatedAt().plusMinutes(gracePeriodMinutes);
        return LocalDateTime.now().isBefore(cutoff);
    }

    private void logActivity(String action, String entityType, UUID entityId, UUID actorId, String details) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setUser(userRepository.getReferenceById(actorId));
        log.setTimestamp(LocalDateTime.now());
        log.setSuccess(true);

        Map<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("entityId", entityId.toString());
        detailsMap.put("actorId", actorId.toString());
        detailsMap.put("details", details);
        log.setDetails(detailsMap);

        activityLogRepository.save(log);
    }
}
