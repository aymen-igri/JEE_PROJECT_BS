package com.backend.backend.entity.subscription;

import com.backend.backend.entity.Base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "plan_name", nullable = false, unique = true, length = 100)
    private String planName;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "billing_cycle", nullable = false, length = 20)
    private String billingCycle;

    @Column(name = "max_doctors", nullable = false)
    private Integer maxDoctors;

    @Column(name = "max_secretary", nullable = false)
    private Integer maxSecretary;

    @Column(name = "features", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> features;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // createdAt inherited from AuditableEntity
}