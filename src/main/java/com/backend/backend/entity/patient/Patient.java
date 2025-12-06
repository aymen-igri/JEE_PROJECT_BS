package com.backend.backend.entity.patient;

import com.backend.backend.entity.User.Secretary;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "cin", unique = true, length = 20)
    private String cin;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by", insertable = false, updatable = false)
    private Secretary registeredBySecretary;



    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;
}
