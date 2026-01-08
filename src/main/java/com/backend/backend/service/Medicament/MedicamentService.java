package com.backend.backend.service.Medicament;

import com.backend.backend.dto.request.Medicament.CreateMedicamentRequest;
import com.backend.backend.dto.request.Medicament.UpdateMedicamentRequest;
import com.backend.backend.dto.response.Medicament.MedicamentResponse;
import com.backend.backend.entity.User.User;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.perscription.Medicament;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.prescription.MedicamentRepository;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing medicaments.
 * Only admins and super admins can create, update, and deactivate medicaments.
 */
@Service
public class MedicamentService {

    private final MedicamentRepository medicamentRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    public MedicamentService(
            MedicamentRepository medicamentRepository,
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository
    ) {
        this.medicamentRepository = medicamentRepository;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Creates a new medicament.
     * Only admins/super admins can perform this action.
     */
    @Transactional
    public MedicamentResponse createMedicament(UUID adminId, CreateMedicamentRequest request) {
        // Validate admin exists
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));

        // Check if medicament with same name already exists
        if (medicamentRepository.existsByNameIgnoreCase(request.name())) {
            throw new IllegalArgumentException("Medicament with name '" + request.name() + "' already exists");
        }

        // Create medicament
        Medicament medicament = Medicament.builder()
                .name(request.name())
                .dosage(request.dosage())
                .form(request.form())
                .category(request.category())
                .isActive(true)
                .createdById(adminId)
                .build();

        Medicament savedMedicament = medicamentRepository.save(medicament);

        // Log activity
        logActivity("Medicament created", "Medicament", savedMedicament.getMedicamentId(), adminId,
                "Created medicament: " + request.name());

        return toResponse(savedMedicament, admin.getFullName());
    }

    /**
     * Updates an existing medicament.
     */
    @Transactional
    public MedicamentResponse updateMedicament(UUID adminId, UUID medicamentId, UpdateMedicamentRequest request) {
        // Validate admin exists
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));

        // Find medicament
        Medicament medicament = medicamentRepository.findByMedicamentId(medicamentId)
                .orElseThrow(() -> new IllegalArgumentException("Medicament not found with ID: " + medicamentId));

        // Check name uniqueness if changing name
        if (request.name() != null && !request.name().equalsIgnoreCase(medicament.getName())) {
            if (medicamentRepository.existsByNameIgnoreCase(request.name())) {
                throw new IllegalArgumentException("Medicament with name '" + request.name() + "' already exists");
            }
            medicament.setName(request.name());
        }

        // Update fields if provided
        if (request.dosage() != null) {
            medicament.setDosage(request.dosage());
        }
        if (request.form() != null) {
            medicament.setForm(request.form());
        }
        if (request.category() != null) {
            medicament.setCategory(request.category());
        }
        if (request.isActive() != null) {
            medicament.setIsActive(request.isActive());
        }

        Medicament savedMedicament = medicamentRepository.save(medicament);

        // Log activity
        logActivity("Medicament updated", "Medicament", savedMedicament.getMedicamentId(), adminId,
                "Updated medicament: " + savedMedicament.getName());

        // Get creator name
        String creatorName = null;
        if (savedMedicament.getCreatedById() != null) {
            creatorName = userRepository.findById(savedMedicament.getCreatedById())
                    .map(User::getFullName)
                    .orElse(null);
        }

        return toResponse(savedMedicament, creatorName);
    }

    /**
     * Deactivates a medicament (soft delete).
     * Medicaments should not be hard deleted as they may be referenced by prescriptions.
     */
    @Transactional
    public MedicamentResponse deactivateMedicament(UUID adminId, UUID medicamentId) {
        // Validate admin exists
        userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));

        // Find medicament
        Medicament medicament = medicamentRepository.findByMedicamentId(medicamentId)
                .orElseThrow(() -> new IllegalArgumentException("Medicament not found with ID: " + medicamentId));

        medicament.setIsActive(false);
        Medicament savedMedicament = medicamentRepository.save(medicament);

        // Log activity
        logActivity("Medicament deactivated", "Medicament", savedMedicament.getMedicamentId(), adminId,
                "Deactivated medicament: " + savedMedicament.getName());

        // Get creator name
        String creatorName = null;
        if (savedMedicament.getCreatedById() != null) {
            creatorName = userRepository.findById(savedMedicament.getCreatedById())
                    .map(User::getFullName)
                    .orElse(null);
        }

        return toResponse(savedMedicament, creatorName);
    }

    /**
     * Reactivates a medicament.
     */
    @Transactional
    public MedicamentResponse reactivateMedicament(UUID adminId, UUID medicamentId) {
        // Validate admin exists
        userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with ID: " + adminId));

        // Find medicament
        Medicament medicament = medicamentRepository.findByMedicamentId(medicamentId)
                .orElseThrow(() -> new IllegalArgumentException("Medicament not found with ID: " + medicamentId));

        medicament.setIsActive(true);
        Medicament savedMedicament = medicamentRepository.save(medicament);

        // Log activity
        logActivity("Medicament reactivated", "Medicament", savedMedicament.getMedicamentId(), adminId,
                "Reactivated medicament: " + savedMedicament.getName());

        // Get creator name
        String creatorName = null;
        if (savedMedicament.getCreatedById() != null) {
            creatorName = userRepository.findById(savedMedicament.getCreatedById())
                    .map(User::getFullName)
                    .orElse(null);
        }

        return toResponse(savedMedicament, creatorName);
    }

    /**
     * Gets a medicament by ID.
     */
    @Transactional(readOnly = true)
    public MedicamentResponse getMedicamentById(UUID medicamentId) {
        Medicament medicament = medicamentRepository.findByMedicamentId(medicamentId)
                .orElseThrow(() -> new IllegalArgumentException("Medicament not found with ID: " + medicamentId));

        // Get creator name
        String creatorName = null;
        if (medicament.getCreatedById() != null) {
            creatorName = userRepository.findById(medicament.getCreatedById())
                    .map(User::getFullName)
                    .orElse(null);
        }

        return toResponse(medicament, creatorName);
    }

    /**
     * Gets all medicaments with pagination.
     */
    @Transactional(readOnly = true)
    public Page<MedicamentResponse> getAllMedicaments(int page, int size, boolean includeInactive) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Medicament> medicaments;
        if (includeInactive) {
            medicaments = medicamentRepository.findAll(pageable);
        } else {
            medicaments = medicamentRepository.findByIsActiveTrue(pageable);
        }

        return medicaments.map(m -> {
            String creatorName = null;
            if (m.getCreatedById() != null) {
                creatorName = userRepository.findById(m.getCreatedById())
                        .map(User::getFullName)
                        .orElse(null);
            }
            return toResponse(m, creatorName);
        });
    }

    /**
     * Searches medicaments by name.
     */
    @Transactional(readOnly = true)
    public List<MedicamentResponse> searchMedicaments(String query) {
        return medicamentRepository.searchByName(query).stream()
                .map(m -> {
                    String creatorName = null;
                    if (m.getCreatedById() != null) {
                        creatorName = userRepository.findById(m.getCreatedById())
                                .map(User::getFullName)
                                .orElse(null);
                    }
                    return toResponse(m, creatorName);
                })
                .collect(Collectors.toList());
    }

    private MedicamentResponse toResponse(Medicament medicament, String createdByName) {
        return new MedicamentResponse(
                medicament.getMedicamentId(),
                medicament.getName(),
                medicament.getDosage(),
                medicament.getForm(),
                medicament.getCategory(),
                medicament.getIsActive(),
                medicament.getCreatedById(),
                createdByName,
                medicament.getCreatedAt(),
                medicament.getUpdatedAt()
        );
    }

    private void logActivity(String action, String entityType, UUID entityId, UUID userId, String details) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId != null ? entityId.toString() : null);
        log.setTimestamp(LocalDateTime.now());
        log.setSuccess(true);

        // Set user if exists
        if (userId != null) {
            userRepository.findById(userId).ifPresent(log::setUser);
        }

        // Set details as a map
        if (details != null) {
            log.setDetails(java.util.Map.of("message", details));
        }

        activityLogRepository.save(log);
    }
}

