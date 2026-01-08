package com.backend.backend.entity.perscription;

import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.User.User;
import com.backend.backend.enums.MedicamentForm;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "medicaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "medicament_id")
    private UUID medicamentId;

    @Column(name = "name", nullable = false, unique = true, length = 200)
    private String name;

    @Column(name = "dosage", length = 50)
    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(name = "form", length = 30)
    private MedicamentForm form;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_by")
    private UUID createdById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;

    //createdAt and updatedAt inherited from AuditableEntity
}