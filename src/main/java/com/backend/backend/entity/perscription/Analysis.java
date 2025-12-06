package com.backend.backend.entity.perscription;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "analysis_id")
    private Integer analysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", insertable = false, updatable = false)
    private Prescription prescription;

    @Column(name = "analysis_type", length = 100)
    private String analysisType;

    @Column(name = "analysis_name", length = 200)
    private String analysisName;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "results", columnDefinition = "TEXT")
    private String results;

    @Column(name = "result_file_path", length = 500)
    private String resultFilePath;
}