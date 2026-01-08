package com.backend.backend.service.Consultation;

import com.backend.backend.dto.request.Consultation.AnalysisRequest;
import com.backend.backend.dto.request.Consultation.CreatePrescriptionRequest;
import com.backend.backend.dto.request.Consultation.PrescriptionItemRequest;
import com.backend.backend.dto.response.Consultation.PrescriptionResponse;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.entity.perscription.Analysis;
import com.backend.backend.entity.perscription.Medicament;
import com.backend.backend.entity.perscription.Prescription;
import com.backend.backend.entity.perscription.PrescriptionItem;
import com.backend.backend.enums.ConsultationStatus;
import com.backend.backend.mapper.Consultation.PrescriptionMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.consultation.ConsultationRepository;
import com.backend.backend.repository.prescription.AnalysisRepository;
import com.backend.backend.repository.prescription.MedicamentRepository;
import com.backend.backend.repository.prescription.PrescriptionItemRepository;
import com.backend.backend.repository.prescription.PrescriptionRepository;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Prescription management.
 * Handles creation and retrieval of prescriptions with items and analyses.
 * All operations are logged for audit trail.
 * NOTE: Prescriptions are immutable once created - no updates or deletes.
 */
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final AnalysisRepository analysisRepository;
    private final ConsultationRepository consultationRepository;
    private final MedicamentRepository medicamentRepository;
    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository,
            AnalysisRepository analysisRepository,
            ConsultationRepository consultationRepository,
            MedicamentRepository medicamentRepository,
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository,
            PrescriptionMapper prescriptionMapper
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.analysisRepository = analysisRepository;
        this.consultationRepository = consultationRepository;
        this.medicamentRepository = medicamentRepository;
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    /**
     * Creates a new prescription with items and analyses.
     * Constraint: Can only add prescriptions to IN_PROGRESS consultations.
     * All entities are saved in a single transaction.
     *
     * @param doctorId The authenticated doctor's ID
     * @param request The creation request with nested items and analyses
     * @return PrescriptionResponse
     * @throws IllegalArgumentException if consultation or medicaments not found
     * @throws IllegalStateException if consultation is not IN_PROGRESS
     * @throws AccessDeniedException if doctor doesn't own the consultation
     */
    @Transactional
    public PrescriptionResponse createPrescription(UUID doctorId, CreatePrescriptionRequest request) {
        // Validate consultation exists and doctor owns it
        Consultation consultation = consultationRepository.findByIdAndDoctorId(request.consultationId(), doctorId)
                .orElseThrow(() -> new AccessDeniedException("Consultation not found or you don't have access"));

        // Validate consultation status - must be IN_PROGRESS to add prescriptions
        if (consultation.getStatus() != ConsultationStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Cannot add prescriptions to consultation with status: " + consultation.getStatus() +
                    ". Consultation must be IN_PROGRESS.");
        }

        // Create prescription
        Prescription prescription = prescriptionMapper.toEntity(request, consultation);
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Create prescription items
        List<PrescriptionItem> savedItems = new ArrayList<>();
        if (request.items() != null && !request.items().isEmpty()) {
            // Validate all medicaments exist first
            Set<UUID> medicamentIds = request.items().stream()
                    .map(PrescriptionItemRequest::medicamentId)
                    .collect(Collectors.toSet());

            Map<UUID, Medicament> medicaments = medicamentRepository.findAllById(medicamentIds).stream()
                    .collect(Collectors.toMap(Medicament::getMedicamentId, m -> m));

            // Verify all requested medicaments were found
            for (UUID medicamentId : medicamentIds) {
                if (!medicaments.containsKey(medicamentId)) {
                    throw new IllegalArgumentException("Medicament not found with ID: " + medicamentId);
                }
            }

            // Create items
            for (PrescriptionItemRequest itemRequest : request.items()) {
                Medicament medicament = medicaments.get(itemRequest.medicamentId());
                PrescriptionItem item = prescriptionMapper.toItemEntity(itemRequest, savedPrescription, medicament);
                savedItems.add(prescriptionItemRepository.save(item));
            }
        }

        // Create analyses
        List<Analysis> savedAnalyses = new ArrayList<>();
        if (request.analyses() != null && !request.analyses().isEmpty()) {
            for (AnalysisRequest analysisRequest : request.analyses()) {
                Analysis analysis = prescriptionMapper.toAnalysisEntity(analysisRequest, savedPrescription);
                savedAnalyses.add(analysisRepository.save(analysis));
            }
        }

        // Log activity
        logActivity("PRESCRIPTION_CREATED", "Prescription", savedPrescription.getPrescriptionId(), doctorId,
                "Created prescription for consultation " + consultation.getConsultationId() +
                ". Items: " + savedItems.size() + ", Analyses: " + savedAnalyses.size());

        return prescriptionMapper.toResponse(savedPrescription, savedItems, savedAnalyses);
    }

    /**
     * Gets a prescription by ID with ownership validation.
     *
     * @param doctorId The authenticated doctor's ID
     * @param prescriptionId The prescription ID
     * @return PrescriptionResponse
     * @throws AccessDeniedException if doctor doesn't own the prescription
     */
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(UUID doctorId, UUID prescriptionId) {
        Prescription prescription = prescriptionRepository.findByIdAndDoctorId(prescriptionId, doctorId)
                .orElseThrow(() -> new AccessDeniedException("Prescription not found or you don't have access"));

        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescriptionIdWithMedicament(prescriptionId);
        List<Analysis> analyses = analysisRepository.findByPrescriptionPrescriptionId(prescriptionId);

        return prescriptionMapper.toResponse(prescription, items, analyses);
    }

    /**
     * Gets all prescriptions for a consultation.
     *
     * @param doctorId The authenticated doctor's ID
     * @param consultationId The consultation ID
     * @return List of PrescriptionResponse
     * @throws AccessDeniedException if doctor doesn't own the consultation
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByConsultation(UUID doctorId, UUID consultationId) {
        // Verify doctor owns the consultation
        if (!consultationRepository.existsByConsultationIdAndDoctorUserId(consultationId, doctorId)) {
            throw new AccessDeniedException("Consultation not found or you don't have access");
        }

        List<Prescription> prescriptions = prescriptionRepository.findByConsultationConsultationId(consultationId);

        return prescriptions.stream()
                .map(prescription -> {
                    List<PrescriptionItem> items = prescriptionItemRepository
                            .findByPrescriptionIdWithMedicament(prescription.getPrescriptionId());
                    List<Analysis> analyses = analysisRepository
                            .findByPrescriptionPrescriptionId(prescription.getPrescriptionId());
                    return prescriptionMapper.toResponse(prescription, items, analyses);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets paginated list of prescriptions for a patient.
     *
     * @param doctorId The authenticated doctor's ID
     * @param patientId The patient ID
     * @param pageable Pagination parameters
     * @return Page of PrescriptionResponse
     */
    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getPrescriptionsByPatient(UUID doctorId, UUID patientId, Pageable pageable) {
        return prescriptionRepository.findByPatientIdPaged(patientId, pageable)
                .map(prescription -> {
                    List<PrescriptionItem> items = prescriptionItemRepository
                            .findByPrescriptionIdWithMedicament(prescription.getPrescriptionId());
                    List<Analysis> analyses = analysisRepository
                            .findByPrescriptionPrescriptionId(prescription.getPrescriptionId());
                    return prescriptionMapper.toResponse(prescription, items, analyses);
                });
    }

    /**
     * Searches medicaments by name.
     *
     * @param query The search query
     * @param pageable Pagination parameters
     * @return Page of Medicament (basic info for selection)
     */
    @Transactional(readOnly = true)
    public Page<Medicament> searchMedicaments(String query, Pageable pageable) {
        return medicamentRepository.searchByNamePaged(query, pageable);
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
