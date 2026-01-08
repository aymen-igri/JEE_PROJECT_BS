package com.backend.backend.mapper.Billing;

import com.backend.backend.dto.response.Billing.BillingResponse;
import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.entity.patient.AppointmentBilling;
import com.backend.backend.entity.patient.Patient;
import org.springframework.stereotype.Component;

/**
 * Mapper for AppointmentBilling entity to DTOs.
 * Billing is per appointment, not per consultation.
 */
@Component
public class BillingMapper {

    /**
     * Maps AppointmentBilling entity to BillingResponse DTO.
     * Expects all related entities to be fetched.
     */
    public BillingResponse toBillingResponse(AppointmentBilling billing) {
        Appointment appointment = billing.getAppointment();
        Patient patient = appointment.getPatient();

        return new BillingResponse(
                billing.getBillingId(),
                billing.getReceiptNumber(),
                appointment.getAppointmentId(),
                appointment.getAppointmentDateTime(),
                patient.getFirstName() + " " + patient.getLastName(),
                patient.getCin(),
                appointment.getDoctor().getFullName(),
                appointment.getCabinet().getName(),
                billing.getOriginalPrice(),
                billing.getDiscountAmount(),
                billing.getDiscountReason(),
                billing.getFinalAmount(),
                billing.getPaymentType(),
                billing.getPaymentStatus(),
                billing.getPaymentDate(),
                billing.getProcessedBy().getFullName(),
                billing.getNotes(),
                billing.getPdfPath(),
                billing.getCreatedAt()
        );
    }
}

