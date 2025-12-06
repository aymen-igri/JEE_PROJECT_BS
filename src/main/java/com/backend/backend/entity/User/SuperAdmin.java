package com.backend.backend.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "super_admins")
@DiscriminatorValue("SUPER_ADMIN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdmin extends User {


    @Column(name = "level", nullable = false)
    private Integer level;
}
