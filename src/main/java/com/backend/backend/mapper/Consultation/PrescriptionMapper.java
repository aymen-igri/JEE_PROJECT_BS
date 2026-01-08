package com.backend.backend.mapper.Consultation;

import com.backend.backend.dto.request.Consultation.AnalysisRequest;
import com.backend.backend.dto.request.Consultation.CreatePrescriptionRequest;
import com.backend.backend.dto.request.Consultation.PrescriptionItemRequest;
import com.backend.backend.dto.response.Consultation.AnalysisResponse;
import com.backend.backend.dto.response.Consultation.PrescriptionItemResponse;
import com.backend.backend.dto.response.Consultation.PrescriptionResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.patient.Consultation;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.entity.perscription.Analysis;
import com.backend.backend.entity.perscription.Medicament;
import com.backend.backend.entity.perscription.Prescription;
import com.backend.backend.entity.perscription.PrescriptionItem;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for Prescription and related entities.
 * Stateless, null-safe transformations between entities and DTOs.
 */
@Component
public class PrescriptionMapper {

    /**
     * Maps CreatePrescriptionRequest to Prescription entity.
     * Does NOT map items and analyses - those need separate entities with resolved medicaments.
     *
     * @param request The creation request DTO
     * @param consultation The consultation this prescription belongs to
     * @return New Prescription entity (not persisted)
     */
    public Prescription toEntity(CreatePrescriptionRequest request, Consultation consultation) {
        Prescription prescription = new Prescription();

        prescription.setConsultation(consultation);
        prescription.setDosage(request.dosage());
        prescription.setFrequency(request.frequency());
        prescription.setDuration(request.duration());
        prescription.setInstructions(request.instructions());
        prescription.setPrescribedDate(LocalDate.now());

        return prescription;
    }

    /**
     * Maps PrescriptionItemRequest to PrescriptionItem entity.
     *
     * @param request The item request DTO
     * @param prescription The parent prescription entity
     * @param medicament The medicament entity (must be resolved beforehand)
     * @return New PrescriptionItem entity (not persisted)
     */
    public PrescriptionItem toItemEntity(
            PrescriptionItemRequest request,
            Prescription prescription,
            Medicament medicament
    ) {
        PrescriptionItem item = new PrescriptionItem();

        item.setPrescription(prescription);
        item.setMedicament(medicament);
        item.setQuantity(request.quantity());
        item.setFrequency(request.frequency());
        item.setDuration(request.duration());
        item.setInstructions(request.instructions());

        return item;
    }

    /**
     * Maps AnalysisRequest to Analysis entity.
     *
     * @param request The analysis request DTO
     * @param prescription The parent prescription entity
     * @return New Analysis entity (not persisted)
     */
    public Analysis toAnalysisEntity(AnalysisRequest request, Prescription prescription) {
        Analysis analysis = new Analysis();

        analysis.setPrescription(prescription);
        analysis.setAnalysisType(request.analysisType());
        analysis.setAnalysisName(request.analysisName());
        analysis.setInstructions(request.instructions());
        // results and resultFilePath are null initially - set when results are received

        return analysis;
    }

    /**
     * Maps Prescription entity to PrescriptionResponse DTO.
     * Includes nested items and analyses.
     *
     * @param prescription The prescription entity
     * @param items List of prescription items (with medicaments loaded)
     * @param analyses List of analyses
     * @return PrescriptionResponse DTO
     */
    public PrescriptionResponse toResponse(
            Prescription prescription,
            List<PrescriptionItem> items,
            List<Analysis> analyses
    ) {
        Consultation consultation = prescription.getConsultation();
        Patient patient = consultation != null ? consultation.getPatient() : null;
        Doctor doctor = consultation != null ? consultation.getDoctor() : null;

        List<PrescriptionItemResponse> itemResponses = items != null
                ? items.stream().map(this::toItemResponse).collect(Collectors.toList())
                : Collections.emptyList();

        List<AnalysisResponse> analysisResponses = analyses != null
                ? analyses.stream().map(this::toAnalysisResponse).collect(Collectors.toList())
                : Collections.emptyList();

        return new PrescriptionResponse(
                prescription.getPrescriptionId(),
                consultation != null ? consultation.getConsultationId() : null,
                patient != null ? patient.getPatientId() : null,
                patient != null ? patient.getFirstName() + " " + patient.getLastName() : null,
                doctor != null ? doctor.getUserId() : null,
                doctor != null ? doctor.getFullName() : null,
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getDuration(),
                prescription.getInstructions(),
                prescription.getPrescribedDate(),
                itemResponses,
                analysisResponses,
                prescription.getCreatedAt(),
                prescription.getUpdatedAt()
        );
    }

    /**
     * Maps PrescriptionItem entity to PrescriptionItemResponse DTO.
     *
     * @param item The prescription item entity (with medicament loaded)
     * @return PrescriptionItemResponse DTO
     */
    public PrescriptionItemResponse toItemResponse(PrescriptionItem item) {
        Medicament medicament = item.getMedicament();

        return new PrescriptionItemResponse(
                item.getItemId(),
                medicament != null ? medicament.getMedicamentId() : null,
                medicament != null ? medicament.getName() : null,
                medicament != null ? medicament.getDosage() : null,
                medicament != null ? medicament.getForm() : null,
                item.getQuantity(),
                item.getFrequency(),
                item.getDuration(),
                item.getInstructions()
        );
    }

    /**
     * Maps Analysis entity to AnalysisResponse DTO.
     *
     * @param analysis The analysis entity
     * @return AnalysisResponse DTO
     */
    public AnalysisResponse toAnalysisResponse(Analysis analysis) {
        return new AnalysisResponse(
                analysis.getAnalysisId(),
                analysis.getAnalysisType(),
                analysis.getAnalysisName(),
                analysis.getInstructions(),
                analysis.getResults(),
                analysis.getResultFilePath()
        );
    }
}

