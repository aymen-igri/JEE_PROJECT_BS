package com.backend.backend.entity.User;
import com.backend.backend.entity.practice.Cabinet;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@DiscriminatorValue("DOCTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends Staff {

    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @Column(name = "license_number", unique = true, nullable = false, length = 50)
    private String licenseNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by", insertable = false, updatable = false)
    private Admin registeredByAdmin;

    @Column(name = "diploma_document", length = 500)
    private String diplomaDocument;


    @Column(name = "license_document", length = 500)
    private String licenseDocument;


    @Column(name = "cv_document", length = 500)
    private String cvDocument;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cabinet> consultations = new ArrayList<>();
}