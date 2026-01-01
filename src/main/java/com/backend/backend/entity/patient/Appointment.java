package com.backend.backend.entity.patient;


import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.entity.practice.Cabinet;
import  com.backend.backend.enums.AppointmentType;
import com.backend.backend.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Appointment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "appointment_id")
    private UUID appointmentId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = true, updatable = true)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", insertable = true, updatable = true)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", insertable = true, updatable = true)
    private Cabinet cabinet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_by", insertable = true, updatable = true)
    private Secretary scheduledBySecretary;

    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "duration")
    private Integer duration;
    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", length = 30)
    private AppointmentType appointmentType;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // createdAt and updatedAt inherited from AuditableEntity
}
