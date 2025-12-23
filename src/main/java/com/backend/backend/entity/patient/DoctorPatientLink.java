package com.backend.backend.entity.patient;

import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.User.Secretary;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "doctor_patient_links",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "patient_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DoctorPatientLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "link_id")
    private UUID linkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", insertable = true, updatable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = true, updatable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_by", insertable = true, updatable = false)
    private Secretary linkedBySecretary;

    @Column(name = "linked_date", nullable = false)
    private LocalDate linkedDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";
}