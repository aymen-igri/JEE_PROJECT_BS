package com.backend.backend.service.Doctor;

import com.backend.backend.dto.response.Dashboard.ConsultationCardDTO;
import com.backend.backend.dto.response.Doctor.DoctorDashboardDTO;
import com.backend.backend.dto.response.ScheduleItemDTO;
import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.enums.AppointmentStatus;
import com.backend.backend.repository.Patient.AppointmentRepository;
import com.backend.backend.service.Consultation.ConsultationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ConsultationService consultationService;
    private final AppointmentRepository appointmentRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Set<AppointmentStatus> ACTIVE_STATUSES = EnumSet.of(
            AppointmentStatus.SCHEDULED,
            AppointmentStatus.CONFIRMED
    );

    public DashboardService(
            ConsultationService consultationService,
            AppointmentRepository appointmentRepository
    ) {
        this.consultationService = consultationService;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public DoctorDashboardDTO getDoctorDashboard(UUID doctorId) {
        // Get latest consultations
        List<ConsultationCardDTO> consultations = consultationService
                .getLatestConsultationsForDoctor(doctorId);

        // Get schedule items
        List<ScheduleItemDTO> schedule = getScheduleForDoctor(doctorId);

        return new DoctorDashboardDTO(consultations, schedule);
    }

    private List<ScheduleItemDTO> getScheduleForDoctor(UUID doctorId) {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduleItemDTO> scheduleItems = new ArrayList<>();

        // Find current appointment (IN_PROGRESS)
        Optional<Appointment> currentAppointment = appointmentRepository
                .findCurrentAppointment(doctorId, AppointmentStatus.IN_PROGRESS, now);

        if (currentAppointment.isPresent()) {
            scheduleItems.add(mapToScheduleItemDTO(currentAppointment.get(), now, true));
        }

        // Find next 3 upcoming appointments
        List<Appointment> upcomingAppointments = appointmentRepository
                .findUpcomingAppointments(doctorId, ACTIVE_STATUSES, now)
                .stream()
                .limit(3)
                .collect(Collectors.toList());

        for (Appointment appointment : upcomingAppointments) {
            scheduleItems.add(mapToScheduleItemDTO(appointment, now, false));
        }

        return scheduleItems;
    }

    private ScheduleItemDTO mapToScheduleItemDTO(Appointment appointment, LocalDateTime now, boolean isCurrent) {
        String title = "Patient: " +
                (appointment.getPatient() != null ?
                        appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName()
                        : "Unknown");

        String priority = determinePriority(appointment);

        String happening = isCurrent ? "NOW" : calculateTimeUntil(now, appointment.getAppointmentDateTime());

        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();
        String startTime = appointmentTime.format(TIME_FORMATTER);

        int duration = appointment.getDuration() != null ? appointment.getDuration() : 30;
        LocalDateTime endTime = appointmentTime.plusMinutes(duration);
        String endTimeStr = endTime.format(TIME_FORMATTER);

        return new ScheduleItemDTO(
                title,
                priority,
                happening,
                startTime,
                endTimeStr,
                appointmentTime
        );
    }

    private String determinePriority(Appointment appointment) {
        if (appointment.getAppointmentType() == null) {
            return "Moderate";
        }

        switch (appointment.getAppointmentType()) {
            case EMERGENCY:
                return "High";
            case FOLLOW_UP:
                return "Low";
            default:
                return "Moderate";
        }
    }

    private String calculateTimeUntil(LocalDateTime now, LocalDateTime appointmentTime) {
        Duration duration = Duration.between(now, appointmentTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}