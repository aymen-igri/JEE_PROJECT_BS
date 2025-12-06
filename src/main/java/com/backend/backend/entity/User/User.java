package com.backend.backend.entity.User;
import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.enums.EGender;
import com.backend.backend.enums.EStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "cin", unique = true, length = 20)
    private String CIN;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private EGender gender;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EStatus status = EStatus.ACTIVE;

    @Column(name = "profile_photo", length = 500)
    private String profilePhoto;

}