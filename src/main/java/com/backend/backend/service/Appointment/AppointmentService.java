package com.backend.backend.service.Appointment;

import com.backend.backend.dto.request.Appointment.CancelAppointmentRequest;
import com.backend.backend.dto.request.Appointment.CreateAppointmentRequest;
import com.backend.backend.dto.request.Appointment.RescheduleAppointmentRequest;
import com.backend.backend.dto.response.Appointment.AppointmentResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.entity.patient.Appointment;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.enums.AppointmentStatus;
import com.backend.backend.mapper.Appointment.AppointmentMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import com.backend.backend.repository.patient.AppointmentRepository;
import com.backend.backend.repository.patient.PatientRepository;

import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.user.DoctorRepository;
import com.backend.backend.repository.user.SecretaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {


    private static final Set<AppointmentStatus> EXCLUDED_FOR_CONFLICTS = EnumSet.of(
            AppointmentStatus.CANCELLED,
            AppointmentStatus.NO_SHOW
    );

    // String version for native queries
    private static final List<String> EXCLUDED_FOR_CONFLICTS_STRINGS = List.of(
            AppointmentStatus.CANCELLED.name(),
            AppointmentStatus.NO_SHOW.name()
    );

    private static final Set<AppointmentStatus> ACTIVE_STATUSES = EnumSet.of(
            AppointmentStatus.SCHEDULED,
            AppointmentStatus.IN_PROGRESS
    );
    private static final Set<AppointmentStatus> RESCHEDULABLE_STATUSES = EnumSet.of(
            AppointmentStatus.SCHEDULED
    );
    private static final Set<AppointmentStatus> CANCELLABLE_STATUSES = EnumSet.of(
            AppointmentStatus.SCHEDULED,
            AppointmentStatus.IN_PROGRESS
    );
    private static final int DEFAULT_DURATION_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SecretaryRepository secretaryRepository;
    private final CabinetRepository cabinetRepository;
    private final AppointmentMapper appointmentMapper;
    private final ActivityLogRepository activityLogRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            SecretaryRepository secretaryRepository,
            CabinetRepository cabinetRepository,
            AppointmentMapper appointmentMapper,
            ActivityLogRepository activityLogRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.secretaryRepository = secretaryRepository;
        this.cabinetRepository = cabinetRepository;
        this.appointmentMapper = appointmentMapper;
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public AppointmentResponse createAppointment(UUID secretaryId, CreateAppointmentRequest request) {
        // Validate secretary exists
        Secretary secretary = secretaryRepository.findByUserId(secretaryId);
        if (secretary == null) {
            throw new IllegalArgumentException("Secretary not found with ID: " + secretaryId);
        }

        // === VALIDATION: Cannot schedule appointments in the past ===
        if (request.appointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule appointment in the past. Please select a future date and time.");
        }

        // Validate patient exists
        Patient patient = patientRepository.findPatientByPatientId(request.patientId());
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with ID: " + request.patientId());
        }

        // Validate doctor exists
        Doctor doctor = doctorRepository.findDoctorByUserId(request.doctorId());
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with ID: " + request.doctorId());
        }

        // Validate cabinet exists and is active
        Cabinet cabinet = cabinetRepository.findByCabinetIdAndStatus(request.cabinetId(), "Active")
                .orElseThrow(() -> new IllegalArgumentException("Cabinet not found or inactive with ID: " + request.cabinetId()));

        // Calculate time range for conflict check
        int duration = request.duration() != null ? request.duration() : DEFAULT_DURATION_MINUTES;
        LocalDateTime rangeStart = request.appointmentDateTime();
        LocalDateTime rangeEnd = rangeStart.plusMinutes(duration);

        // Check for doctor conflicts
        List<Appointment> doctorConflicts = appointmentRepository.findPotentialConflicts(
                request.doctorId(), rangeStart, rangeEnd, EXCLUDED_FOR_CONFLICTS_STRINGS
        );
        if (!doctorConflicts.isEmpty()) {
            throw new IllegalArgumentException("Doctor has a conflicting appointment at this time");
        }

        // Check for cabinet conflicts
        List<Appointment> cabinetConflicts = appointmentRepository.findCabinetConflicts(
                request.cabinetId(), rangeStart, rangeEnd, EXCLUDED_FOR_CONFLICTS_STRINGS
        );
        if (!cabinetConflicts.isEmpty()) {
            throw new IllegalArgumentException("Cabinet is occupied at this time");
        }

        // Create appointment entity
        Appointment appointment = appointmentMapper.toAppointment(request);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setCabinet(cabinet);
        appointment.setScheduledBySecretary(secretary);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // Save appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        logActivity("Appointment scheduled", "Appointment", savedAppointment.getAppointmentId(),
                "Appointment scheduled for patient " + patient.getFirstName() + " " + patient.getLastName() +
                " with Dr. " + doctor.getFullName() + " by secretary " + secretary.getFullName());

        return appointmentMapper.toAppointmentResponse(savedAppointment);
    }

    @Transactional
    public AppointmentResponse rescheduleAppointment(UUID secretaryId, RescheduleAppointmentRequest request) {
        // Validate secretary exists
        Secretary secretary = secretaryRepository.findByUserId(secretaryId);
        if (secretary == null) {
            throw new IllegalArgumentException("Secretary not found with ID: " + secretaryId);
        }

        // === VALIDATION: Cannot reschedule to a past date ===
        if (request.newDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot reschedule appointment to a past date and time. Please select a future date.");
        }

        // Fetch appointment with details
        Appointment appointment = appointmentRepository.findByAppointmentIdWithDetails(request.appointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + request.appointmentId()));

        // Validate secretary ownership
        if (appointment.getScheduledBySecretary() == null ||
                !appointment.getScheduledBySecretary().getUserId().equals(secretaryId)) {
            throw new IllegalArgumentException("Unauthorized: You did not schedule this appointment");
        }

        // Validate status allows rescheduling
        if (!RESCHEDULABLE_STATUSES.contains(appointment.getStatus())) {
            throw new IllegalArgumentException("Cannot reschedule appointment with status: " + appointment.getStatus());
        }

        // Calculate new time range for conflict check
        int duration = request.newDuration() != null ? request.newDuration() :
                       (appointment.getDuration() != null ? appointment.getDuration() : DEFAULT_DURATION_MINUTES);
        LocalDateTime rangeStart = request.newDateTime();
        LocalDateTime rangeEnd = rangeStart.plusMinutes(duration);

        // Check for doctor conflicts (excluding current appointment)
        List<Appointment> doctorConflicts = appointmentRepository.findPotentialConflicts(
                appointment.getDoctor().getUserId(), rangeStart, rangeEnd, EXCLUDED_FOR_CONFLICTS_STRINGS
        ).stream()
                .filter(a -> !a.getAppointmentId().equals(appointment.getAppointmentId()))
                .collect(Collectors.toList());

        if (!doctorConflicts.isEmpty()) {
            throw new IllegalArgumentException("Doctor has a conflicting appointment at the new time");
        }

        // Check for cabinet conflicts (excluding current appointment)
        List<Appointment> cabinetConflicts = appointmentRepository.findCabinetConflicts(
                appointment.getCabinet().getCabinetId(), rangeStart, rangeEnd, EXCLUDED_FOR_CONFLICTS_STRINGS
        ).stream()
                .filter(a -> !a.getAppointmentId().equals(appointment.getAppointmentId()))
                .collect(Collectors.toList());

        if (!cabinetConflicts.isEmpty()) {
            throw new IllegalArgumentException("Cabinet is occupied at the new time");
        }

        // Store old datetime for logging
        LocalDateTime oldDateTime = appointment.getAppointmentDateTime();

        // Update appointment
        appointment.setAppointmentDateTime(request.newDateTime());
        if (request.newDuration() != null) {
            appointment.setDuration(request.newDuration());
        }
        if (request.reason() != null && !request.reason().isBlank()) {
            String existingNotes = appointment.getNotes() != null ? appointment.getNotes() + "\n" : "";
            appointment.setNotes(existingNotes + "[Rescheduled] " + request.reason());
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Log activity
        logActivity("Appointment rescheduled", "Appointment", savedAppointment.getAppointmentId(),
                "Appointment rescheduled from " + oldDateTime + " to " + request.newDateTime() +
                " by secretary " + secretary.getFullName());

        return appointmentMapper.toAppointmentResponse(savedAppointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(UUID secretaryId, CancelAppointmentRequest request) {
        // Validate secretary exists
        Secretary secretary = secretaryRepository.findByUserId(secretaryId);
        if (secretary == null) {
            throw new IllegalArgumentException("Secretary not found with ID: " + secretaryId);
        }

        // Fetch appointment with details
        Appointment appointment = appointmentRepository.findByAppointmentIdWithDetails(request.appointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + request.appointmentId()));

        // Validate secretary ownership
        if (appointment.getScheduledBySecretary() == null ||
                !appointment.getScheduledBySecretary().getUserId().equals(secretaryId)) {
            throw new IllegalArgumentException("Unauthorized: You did not schedule this appointment");
        }

        // Validate status allows cancellation
        if (!CANCELLABLE_STATUSES.contains(appointment.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel appointment with status: " + appointment.getStatus());
        }

        // Update status and notes atomically
        String cancellationNote = "[Cancelled] " + request.cancellationReason();
        String existingNotes = appointment.getNotes() != null ? appointment.getNotes() + "\n" : "";

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setNotes(existingNotes + cancellationNote);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Log activity
        logActivity("Appointment cancelled", "Appointment", savedAppointment.getAppointmentId(),
                "Appointment cancelled. Reason: " + request.cancellationReason() +
                " by secretary " + secretary.getFullName());

        return appointmentMapper.toAppointmentResponse(savedAppointment);
    }


    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentIdWithDetails(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        return appointmentMapper.toAppointmentResponse(appointment);
    }


    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsBySecretary(UUID secretaryId) {
        // Validate secretary exists
        Secretary secretary = secretaryRepository.findByUserId(secretaryId);
        if (secretary == null) {
            throw new IllegalArgumentException("Secretary not found with ID: " + secretaryId);
        }

        List<Appointment> appointments = appointmentRepository.findByScheduledBySecretaryUserIdWithDetails(
                secretaryId, EnumSet.noneOf(AppointmentStatus.class)
        );

        return appointments.stream()
                .map(appointmentMapper::toAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getUpcomingAppointmentsForDoctor(UUID doctorId) {
        // Validate doctor exists
        Doctor doctor = doctorRepository.findDoctorByUserId(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        List<Appointment> appointments = appointmentRepository.findUpcomingByDoctorUserId(
                doctorId, LocalDateTime.now(), ACTIVE_STATUSES
        );

        return appointments.stream()
                .map(appointmentMapper::toAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getTodayAppointmentsForDoctor(UUID doctorId) {
        // Validate doctor exists
        Doctor doctor = doctorRepository.findDoctorByUserId(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        LocalDateTime dayStart = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.now().with(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findTodayByDoctorUserId(
                doctorId, dayStart, dayEnd, EXCLUDED_FOR_CONFLICTS
        );

        return appointments.stream()
                .map(appointmentMapper::toAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByPatient(UUID patientId) {
        // Validate patient exists
        Patient patient = patientRepository.findPatientByPatientId(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientPatientIdWithDetails(
                patientId, EnumSet.noneOf(AppointmentStatus.class)
        );

        return appointments.stream()
                .map(appointmentMapper::toAppointmentResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse completeAppointment(UUID doctorId, UUID appointmentId, String notes) {
        // Validate doctor exists
        Doctor doctor = doctorRepository.findDoctorByUserId(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        // Fetch appointment
        Appointment appointment = appointmentRepository.findByAppointmentIdWithDetails(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        // Validate doctor ownership
        if (!appointment.getDoctor().getUserId().equals(doctorId)) {
            throw new IllegalArgumentException("Unauthorized: This appointment is not assigned to you");
        }

        // Validate status
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED &&
            appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Cannot complete appointment with status: " + appointment.getStatus());
        }

        // Update appointment
        appointment.setStatus(AppointmentStatus.COMPLETED);
        if (notes != null && !notes.isBlank()) {
            String existingNotes = appointment.getNotes() != null ? appointment.getNotes() + "\n" : "";
            appointment.setNotes(existingNotes + "[Completed] " + notes);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Log activity
        logActivity("Appointment completed", "Appointment", savedAppointment.getAppointmentId(),
                "Appointment marked as completed by Dr. " + doctor.getFullName());

        return appointmentMapper.toAppointmentResponse(savedAppointment);
    }

    /**
     * Allows a secretary to complete an appointment they scheduled.
     * Secretaries can only complete appointments they scheduled.
     *
     * @param secretaryId The ID of the secretary
     * @param appointmentId The ID of the appointment to complete
     * @param notes Optional notes
     * @return The updated appointment response
     */
    @Transactional
    public AppointmentResponse completeAppointmentBySecretary(UUID secretaryId, UUID appointmentId, String notes) {
        // Validate secretary exists
        Secretary secretary = secretaryRepository.findByUserId(secretaryId);
        if (secretary == null) {
            throw new IllegalArgumentException("Secretary not found with ID: " + secretaryId);
        }

        // Fetch appointment
        Appointment appointment = appointmentRepository.findByAppointmentIdWithDetails(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        // Validate secretary scheduled this appointment
        if (appointment.getScheduledBySecretary() == null ||
                !appointment.getScheduledBySecretary().getUserId().equals(secretaryId)) {
            throw new IllegalArgumentException("Unauthorized: You did not schedule this appointment");
        }

        // Validate status
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED &&
            appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Cannot complete appointment with status: " + appointment.getStatus());
        }

        // Update appointment
        appointment.setStatus(AppointmentStatus.COMPLETED);
        if (notes != null && !notes.isBlank()) {
            String existingNotes = appointment.getNotes() != null ? appointment.getNotes() + "\n" : "";
            appointment.setNotes(existingNotes + "[Completed by Secretary] " + notes);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Log activity
        logActivity("Appointment completed by secretary", "Appointment", savedAppointment.getAppointmentId(),
                "Appointment marked as completed by secretary " + secretary.getFullName());

        return appointmentMapper.toAppointmentResponse(savedAppointment);
    }

    /**
     * Allows a doctor to cancel their own appointment.
     * Doctors can only cancel appointments assigned to them that are in SCHEDULED or IN_PROGRESS status.
     *
     * @param doctorId The ID of the doctor requesting cancellation
     * @param appointmentId The ID of the appointment to cancel
     * @param cancellationReason The reason for cancellation
     * @return The updated appointment response
     */
    @Transactional
    public AppointmentResponse cancelAppointmentByDoctor(UUID doctorId, UUID appointmentId, String cancellationReason) {
        // Validate doctor exists
        Doctor doctor = doctorRepository.findDoctorByUserId(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }

        // Fetch appointment with details
        Appointment appointment = appointmentRepository.findByAppointmentIdWithDetails(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        // Validate doctor ownership - only the assigned doctor can cancel
        if (!appointment.getDoctor().getUserId().equals(doctorId)) {
            throw new SecurityException("Unauthorized: This appointment is not assigned to you");
        }

        // Validate status allows cancellation
        if (!CANCELLABLE_STATUSES.contains(appointment.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel appointment with status: " + appointment.getStatus());
        }

        // Update status and notes atomically
        String cancellationNote = "[Cancelled by Doctor] " + (cancellationReason != null ? cancellationReason : "No reason provided");
        String existingNotes = appointment.getNotes() != null ? appointment.getNotes() + "\n" : "";

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setNotes(existingNotes + cancellationNote);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Log activity
        logActivity("Appointment cancelled by doctor", "Appointment", savedAppointment.getAppointmentId(),
                "Appointment cancelled by Dr. " + doctor.getFullName() + ". Reason: " + cancellationReason);

        return appointmentMapper.toAppointmentResponse(savedAppointment);
    }

    private void logActivity(String action, String entityType, UUID entityId, String details) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }
}

