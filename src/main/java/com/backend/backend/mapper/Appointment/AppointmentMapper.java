package com.backend.backend.mapper.Appointment;

import com.backend.backend.dto.request.Appointment.CreateAppointmentRequest;
import com.backend.backend.dto.response.Appointment.AppointmentResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.enums.AppointmentStatus;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {




    public Appointment toAppointment(CreateAppointmentRequest request) {
        Appointment appointment = new Appointment();

        appointment.setAppointmentDateTime(request.appointmentDateTime());
        appointment.setDuration(request.duration());
        appointment.setAppointmentType(request.appointmentType());
        appointment.setReason(request.reason());
        appointment.setNotes(request.notes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointment;
    }

    public AppointmentResponse toAppointmentResponse(Appointment appointment) {
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        Cabinet cabinet = appointment.getCabinet();
        Secretary secretary = appointment.getScheduledBySecretary();

        return new AppointmentResponse(
                appointment.getAppointmentId(),
                patient != null ? patient.getPatientId() : null,
                patient != null ? patient.getFirstName() + " " + patient.getLastName() : null,
                doctor != null ? doctor.getUserId() : null,
                doctor != null ? doctor.getFullName() : null,
                cabinet != null ? cabinet.getCabinetId() : null,
                cabinet != null ? cabinet.getName() : null,
                appointment.getAppointmentDateTime(),
                appointment.getDuration(),
                appointment.getAppointmentType(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getNotes(),
                secretary != null ? secretary.getUserId() : null,
                secretary != null ? secretary.getFullName() : null,
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}

