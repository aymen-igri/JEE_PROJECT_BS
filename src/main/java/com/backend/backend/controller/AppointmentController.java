package com.backend.backend.controller;

import com.backend.backend.dto.request.Appointment.CancelAppointmentRequest;
import com.backend.backend.dto.request.Appointment.CreateAppointmentRequest;
import com.backend.backend.dto.request.Appointment.RescheduleAppointmentRequest;
import com.backend.backend.dto.response.Appointment.AppointmentResponse;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Appointment.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        UUID secretaryId = userDetails.getUser().getUserId();
        AppointmentResponse response = appointmentService.createAppointment(secretaryId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @PutMapping("/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RescheduleAppointmentRequest request
    ) {
        UUID secretaryId = userDetails.getUser().getUserId();
        AppointmentResponse response = appointmentService.rescheduleAppointment(secretaryId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @PutMapping("/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CancelAppointmentRequest request
    ) {
        UUID secretaryId = userDetails.getUser().getUserId();
        AppointmentResponse response = appointmentService.cancelAppointment(secretaryId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SECRETARY')")
    @GetMapping("/my-scheduled")
    public ResponseEntity<List<AppointmentResponse>> getMyScheduledAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID secretaryId = userDetails.getUser().getUserId();
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsBySecretary(secretaryId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Allows a secretary to complete an appointment they scheduled.
     * Appointment is marked done by either doc or secretary, or when payment is processed.
     */
    @PreAuthorize("hasRole('SECRETARY')")
    @PutMapping("/{appointmentId}/secretary-complete")
    public ResponseEntity<AppointmentResponse> completeAppointmentBySecretary(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String notes
    ) {
        UUID secretaryId = userDetails.getUser().getUserId();
        AppointmentResponse response = appointmentService.completeAppointmentBySecretary(secretaryId, appointmentId, notes);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        List<AppointmentResponse> appointments = appointmentService.getUpcomingAppointmentsForDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/today")
    public ResponseEntity<List<AppointmentResponse>> getTodayAppointments(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        List<AppointmentResponse> appointments = appointmentService.getTodayAppointmentsForDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String notes
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        AppointmentResponse response = appointmentService.completeAppointment(doctorId, appointmentId, notes);
        return ResponseEntity.ok(response);
    }

    /**
     * Allows a doctor to cancel their own appointment.
     * Only the assigned doctor can cancel an appointment via this endpoint.
     */
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{appointmentId}/doctor-cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointmentByDoctor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String cancellationReason
    ) {
        UUID doctorId = userDetails.getUser().getUserId();
        AppointmentResponse response = appointmentService.cancelAppointmentByDoctor(doctorId, appointmentId, cancellationReason);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'DOCTOR')")
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable UUID appointmentId
    ) {
        AppointmentResponse response = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SECRETARY', 'DOCTOR')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(
            @PathVariable UUID patientId
    ) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }
}

