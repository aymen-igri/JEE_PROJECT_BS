package com.backend.backend.entity.practice;


import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.User.Staff;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * From diagram: AffectationRequest
 * Fields:
 * - requestID: int
 * - cabinateID: int (typo in diagram)
 * - applicantUserID: int
 * - role: string
 * - status: string
 * - requestDate: Date
 * - processedDate: Date
 * - message: string
 * - rejectionReason: string
 * - processedBy: int
 *
 * Relationships:
 * - Staff "create" AffectationRequest (1 to *)
 * - Doctor "Processed by" AffectationRequest (1 to 1...*)
 * - AffectationRequest "To join" Cabinet (* to 1)
 */
@Entity
@Table(name = "affectation_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffectationRequest extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "request_id")
    private Integer requestId;

    @Column(name = "cabinet_id", nullable = false)
    private Integer cabinetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", insertable = false, updatable = false)
    private Cabinet cabinet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_user_id", insertable = false, updatable = false)
    private Staff applicant;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "processed_date")
    private LocalDate processedDate;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "processed_by")
    private Integer processedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by", insertable = false, updatable = false)
    private Doctor processedByDoctor;
}