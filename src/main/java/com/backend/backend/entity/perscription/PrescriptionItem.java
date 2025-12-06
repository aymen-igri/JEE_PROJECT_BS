package com.backend.backend.entity.perscription;



import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private UUID itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", insertable = false, updatable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicament_id", insertable = false, updatable = false)
    private Medicament medicament;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "frequency", length = 100)
    private String frequency;

    @Column(name = "duration", length = 50)
    private String duration;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
}