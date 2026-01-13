package com.backend.backend.service.Consultation;

import com.backend.backend.dto.request.Consultation.CreateConsultationRequest;
import com.backend.backend.dto.request.Consultation.UpdateConsultationRequest;
import com.backend.backend.dto.response.Consultation.ConsultationDetailResponse;
import com.backend.backend.dto.response.Consultation.ConsultationResponse;
import com.backend.backend.dto.response.Consultation.DiagnosticResponse;
import com.backend.backend.dto.response.Consultation.PrescriptionResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.entity.patient.Diagnostic;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.entity.perscription.Analysis;
import com.backend.backend.entity.perscription.Prescription;
import com.backend.backend.entity.perscription.PrescriptionItem;
import com.backend.backend.enums.ConsultationStatus;
import com.backend.backend.mapper.Consultation.ConsultationMapper;
import com.backend.backend.mapper.Consultation.DiagnosticMapper;
import com.backend.backend.mapper.Consultation.PrescriptionMapper;
import com.backend.backend.repository.Patient.PatientRepository;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.consultation.ConsultationRepository;
import com.backend.backend.repository.consultation.DiagnosticRepository;
import com.backend.backend.repository.prescription.AnalysisRepository;
import com.backend.backend.repository.prescription.PrescriptionItemRepository;
import com.backend.backend.repository.prescription.PrescriptionRepository;
import com.backend.backend.repository.user.DoctorRepository;
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
import java.util.stream.Collectors;

/**
 * Service for Consultation management.
 * Consultations are created via patient ID and have no date.
 * A consultation can span multiple appointments.
 * Handles creation, update, retrieval of consultations with security validation.
 * All operations are logged for audit trail.
 */
@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final AnalysisRepository analysisRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final ConsultationMapper consultationMapper;
    private final DiagnosticMapper diagnosticMapper;
    private final PrescriptionMapper prescriptionMapper;

    @Value("${app.diagnostic.grace-period-minutes:30}")
    private int gracePeriodMinutes;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    public ConsultationService(
            ConsultationRepository consultationRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DiagnosticRepository diagnosticRepository,
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository,
            AnalysisRepository analysisRepository,
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository,
            ConsultationMapper consultationMapper,
            DiagnosticMapper diagnosticMapper,
            PrescriptionMapper prescriptionMapper
    ) {
        this.consultationRepository = consultationRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.diagnosticRepository = diagnosticRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.analysisRepository = analysisRepository;
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.consultationMapper = consultationMapper;
        this.diagnosticMapper = diagnosticMapper;
        this.prescriptionMapper = prescriptionMapper;
    }

    /**
     * Creates a new consultation for a patient.
     * Consultations are created via patient ID.
     * Consultations have no date - they are tracked only by status.
     *
     * @param doctorId The authenticated doctor's ID
     * @param request The creation request
     * @return ConsultationResponse
     * @throws IllegalArgumentException if patient not found
     */
    @Transactional
    public ConsultationResponse createConsultation(UUID doctorId, CreateConsultationRequest request) {
        // Validate doctor exists
        Doctor doctor = doctorRepository.findDoctorByUserId(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        // Validate patient exists
        Patient patient = patientRepository.findPatientByPatientId(request.patientId());
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with ID: " + request.patientId());
        }

        // Create consultation
        Consultation consultation = consultationMapper.toEntity(request, doctor, patient);
        Consultation saved = consultationRepository.save(consultation);

        // Log activity
        logActivity("CONSULTATION_CREATED", "Consultation", saved.getConsultationId(), doctorId,
                "Created consultation for patient " + patient.getFirstName() + " " + patient.getLastName());

        return consultationMapper.toResponse(saved);
    }

    /**
     * Updates an existing consultation.
     * Enforces the following constraints:
     * - Cannot modify COMPLETED or CANCELLED consultations (except notes append)
     * - Status can only transition: IN_PROGRESS → COMPLETED or CANCELLED
     * - Clinical data (symptoms, vital signs, etc.) can only be modified when IN_PROGRESS
     *
     * @param doctorId The authenticated doctor's ID
     * @param consultationId The consultation to update
     * @param request The update request
     * @return ConsultationResponse
     * @throws IllegalArgumentException if consultation not found
     * @throws IllegalStateException if update violates business rules
     * @throws AccessDeniedException if doctor doesn't own the consultation
     */
    @Transactional
    public ConsultationResponse updateConsultation(UUID doctorId, UUID consultationId, UpdateConsultationRequest request) {
        // Fetch consultation with ownership validation
        Consultation consultation = consultationRepository.findByIdAndDoctorId(consultationId, doctorId)
                .orElseThrow(() -> new AccessDeniedException("Consultation not found or you don't have access"));

        ConsultationStatus currentStatus = consultation.getStatus();

        // Store old values for logging
        Map<String, Object> oldValues = new HashMap<>();
        oldValues.put("status", currentStatus);

        // === VALIDATION 1: Status Transition ===
        if (request.status() != null && request.status() != currentStatus) {
            validateStatusTransition(currentStatus, request.status());
        }

        // === VALIDATION 2: Block modifications to final states (except notes) ===
        if (isFinalState(currentStatus)) {
            if (hasClinicalDataChanges(request)) {
                throw new IllegalStateException(
                        "Cannot modify consultation with status: " + currentStatus +
                        ". Only notes can be updated after consultation is finalized.");
            }
        }

        // === VALIDATION 3: Clinical data modification only in IN_PROGRESS ===
        if (hasClinicalDataChanges(request) && currentStatus != ConsultationStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Clinical data can only be modified when consultation is IN_PROGRESS. Current status: " + currentStatus);
        }

        // Update consultation
        consultationMapper.updateEntity(request, consultation);
        Consultation saved = consultationRepository.save(consultation);

        // Log activity
        logActivity("CONSULTATION_UPDATED", "Consultation", saved.getConsultationId(), doctorId,
                "Updated consultation. Previous status: " + oldValues.get("status") + ", New status: " + saved.getStatus());

        return consultationMapper.toResponse(saved);
    }

    /**
     * Validates status transition rules.
     * - Cannot transition from final states (COMPLETED, CANCELLED)
     * - IN_PROGRESS can only go to COMPLETED or CANCELLED
     */
    private void validateStatusTransition(ConsultationStatus currentStatus, ConsultationStatus newStatus) {
        if (isFinalState(currentStatus)) {
            throw new IllegalStateException(
                    "Cannot change status of " + currentStatus + " consultation. This is a final state.");
        }

        if (currentStatus == ConsultationStatus.IN_PROGRESS) {
            if (newStatus != ConsultationStatus.COMPLETED && newStatus != ConsultationStatus.CANCELLED) {
                throw new IllegalStateException(
                        "Invalid status transition: " + currentStatus + " → " + newStatus +
                        ". IN_PROGRESS can only transition to COMPLETED or CANCELLED.");
            }
        }
    }

    /**
     * Checks if status is a final state (no further transitions allowed).
     */
    private boolean isFinalState(ConsultationStatus status) {
        return status == ConsultationStatus.COMPLETED || status == ConsultationStatus.CANCELLED;
    }

    /**
     * Checks if request contains clinical data changes.
     */
    private boolean hasClinicalDataChanges(UpdateConsultationRequest request) {
        return request.chiefComplaint() != null ||
               request.symptoms() != null ||
               request.vitalSigns() != null ||
               request.physicalExam() != null;
    }

    /**
     * Gets a consultation by ID with ownership validation.
     *
     * @param doctorId The authenticated doctor's ID
     * @param consultationId The consultation ID
     * @return ConsultationResponse
     * @throws AccessDeniedException if doctor doesn't own the consultation
     */
    @Transactional(readOnly = true)
    public ConsultationResponse getConsultationById(UUID doctorId, UUID consultationId) {
        Consultation consultation = consultationRepository.findByIdAndDoctorId(consultationId, doctorId)
                .orElseThrow(() -> new AccessDeniedException("Consultation not found or you don't have access"));

        return consultationMapper.toResponse(consultation);
    }

    /**
     * Gets a consultation with full details including diagnostics and prescriptions.
     *
     * @param doctorId The authenticated doctor's ID
     * @param consultationId The consultation ID
     * @return ConsultationDetailResponse
     * @throws AccessDeniedException if doctor doesn't own the consultation
     */
    @Transactional(readOnly = true)
    public ConsultationDetailResponse getConsultationDetail(UUID doctorId, UUID consultationId) {
        Consultation consultation = consultationRepository.findByIdAndDoctorId(consultationId, doctorId)
                .orElseThrow(() -> new AccessDeniedException("Consultation not found or you don't have access"));

        // Fetch diagnostics with grace period computation
        List<Diagnostic> diagnostics = diagnosticRepository.findByConsultationConsultationId(consultationId);
        List<DiagnosticResponse> diagnosticResponses = diagnosticMapper.toResponseList(diagnostics, gracePeriodMinutes);

        // Fetch prescriptions with items and analyses
        List<Prescription> prescriptions = prescriptionRepository.findByConsultationConsultationId(consultationId);
        List<PrescriptionResponse> prescriptionResponses = prescriptions.stream()
                .map(p -> {
                    List<PrescriptionItem> items = prescriptionItemRepository.findByPrescriptionIdWithMedicament(p.getPrescriptionId());
                    List<Analysis> analyses = analysisRepository.findByPrescriptionPrescriptionId(p.getPrescriptionId());
                    return prescriptionMapper.toResponse(p, items, analyses);
                })
                .collect(Collectors.toList());

        return consultationMapper.toDetailResponse(consultation, diagnosticResponses, prescriptionResponses);
    }

    /**
     * Gets paginated list of doctor's consultations.
     *
     * @param doctorId The authenticated doctor's ID
     * @param pageable Pagination parameters
     * @return Page of ConsultationResponse
     */
    @Transactional(readOnly = true)
    public Page<ConsultationResponse> getMyConsultations(UUID doctorId, Pageable pageable) {
        return consultationRepository.findByDoctorIdPaged(doctorId, pageable)
                .map(consultationMapper::toResponse);
    }

    /**
     * Gets paginated list of consultations for a specific patient.
     *
     * @param doctorId The authenticated doctor's ID
     * @param patientId The patient ID
     * @param pageable Pagination parameters
     * @return Page of ConsultationResponse
     */
    @Transactional(readOnly = true)
    public Page<ConsultationResponse> getConsultationsByPatient(UUID doctorId, UUID patientId, Pageable pageable) {
        // Only return consultations for patients this doctor has seen
        return consultationRepository.findByPatientIdPaged(patientId, pageable)
                .map(consultationMapper::toResponse);
    }

    /**
     * Gets paginated list of consultations by status.
     *
     * @param doctorId The authenticated doctor's ID
     * @param status The consultation status
     * @param pageable Pagination parameters
     * @return Page of ConsultationResponse
     */




    
    /* MISSING CODE*/
    @Transactional(readOnly = true)
    public Page<ConsultationResponse> getConsultationsByStatus(UUID doctorId, ConsultationStatus status, Pageable pageable) {
        return consultationRepository.findByDoctorIdAndStatusPaged(doctorId, status, pageable)
                .map(consultationMapper::toResponse);
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
    
    /**
     * Logs an activity for audit trail.
     * Logs are INSERT only - never modified or deleted.
     */
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

