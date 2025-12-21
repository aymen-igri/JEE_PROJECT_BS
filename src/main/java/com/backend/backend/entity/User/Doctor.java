package com.backend.backend.entity.User;
import com.backend.backend.entity.practice.Cabinet;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @JoinColumn(name = "registered_by", insertable = true, updatable = false) // aymen is here, changed the insertable from false to true so i can insert the UUID of the admin
    private Admin registeredByAdmin;

    @Column(name = "diploma_document", length = 500)
    private String diplomaDocument;


    @Column(name = "license_document", length = 500)
    private String licenseDocument;
    
    @Column(name = "cv_document", length = 500)
    private String cvDocument;

    // Renamed from 'consultations' to 'cabinets' for clarity
    // LAZY loading prevents N+1 - use JOIN FETCH in repository when needed
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cabinet> cabinets = new ArrayList<>();
}
