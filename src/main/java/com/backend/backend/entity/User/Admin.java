package com.backend.backend.entity.User;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.List;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {

    @Column(name = "permissions", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> permissions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by", insertable = false, updatable = false)
    private SuperAdmin registeredByAdmin;
}
