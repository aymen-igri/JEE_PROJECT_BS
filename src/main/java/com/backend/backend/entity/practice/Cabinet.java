package com.backend.backend.entity.practice;

import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.Base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cabinets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cabinet extends AuditableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cabinet_id")
    private UUID cabinetId;


    @Column(name = "name", nullable = false, length = 100)
    private String name;


    @Column(name = "logo", length = 500)
    private String logo;


    @Column(name = "specialty", length = 100)
    private String specialty;


    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Column(name = "address", length = 255)
    private String address;


    @Column(name = "phone", length = 20)
    private String phone;


    @Column(name = "status", nullable = false, length = 20)
    private String status = "Active";


    @Column(name = "default_consult_price", precision = 10, scale = 2)
    private BigDecimal defaultConsultPrice;


    @Column(name = "created_by")
    private Integer createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // createdAt and updatedAt inherited from AuditableEntity
}