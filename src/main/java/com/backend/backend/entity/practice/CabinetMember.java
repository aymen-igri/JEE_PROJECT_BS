package com.backend.backend.entity.practice;
import com.backend.backend.entity.User.Staff;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cabinet_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "cabinet_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabinetMember {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private UUID memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Staff user;

    @Column(name = "cabinet_id", nullable = false)
    private Integer cabinetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", insertable = false, updatable = false)
    private Cabinet cabinet;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @Column(name = "left_date")
    private LocalDate leftDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}