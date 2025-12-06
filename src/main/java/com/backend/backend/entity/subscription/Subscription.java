package com.backend.backend.entity.subscription;

import com.backend.backend.entity.Base.AuditableEntity;
import com.backend.backend.entity.practice.Cabinet;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "subscription_id")
    private UUID subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", insertable = false, updatable = false)
    private Cabinet cabinet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    private SubscriptionPlan plan;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = false;

    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;

    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;

    @Column(name = "grace_period_end_date")
    private LocalDate gracePeriodEndDate;

    @Column(name = "cancelled_at")
    private LocalDate cancelledAt;

    @Column(name = "cancelled_by")
    private Integer cancelledBy;

    //  createdAt inherited from AuditableEntity
}