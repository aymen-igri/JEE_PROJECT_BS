package com.backend.backend.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "secretaries")
@DiscriminatorValue("SECRETARY")
@Getter
@Setter

@AllArgsConstructor
public class Secretary extends Staff {
// nothing
}
