package com.backend.backend.entity.User;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("STAFF")
@Getter
@Setter
@AllArgsConstructor

public abstract class Staff extends User {
// nothing
}