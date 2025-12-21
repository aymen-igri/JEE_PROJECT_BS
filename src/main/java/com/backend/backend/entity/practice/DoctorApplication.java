package com.backend.backend.entity.practice;


import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.User.Admin;
import com.backend.backend.enums.ApplicationStatus;
import com.backend.backend.repository.practice.DoctorApplicationRepository;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;


import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "doctor_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorApplication  {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "application_id")
    private UUID applicationId;


    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "cin", nullable = false, length = 20)
    private String cin;
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @Column(name = "license_number", nullable = false, length = 50)
    private String licenseNumber;

    @Column(name = "diploma_document", length = 500)
    private String diplomaDocument;

    @Column(name = "license_document", length = 500)
    private String licenseDocument;

    @Column(name = "cv_document", length = 500)
    private String cvDocument;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreatedDate
    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "processed_date")
    private LocalDate processedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by", insertable = true, updatable = false) //aymen is here, changed the insertable from false to true so i can insert the UUID of the admin
    private Admin processedByAdmin;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}