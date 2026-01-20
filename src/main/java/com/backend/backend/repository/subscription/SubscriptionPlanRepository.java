package com.backend.backend.repository.subscription;


import com.backend.backend.entity.subscription.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {

    List<SubscriptionPlan> findByIsActiveTrue();

    Optional<SubscriptionPlan> findByPlanName(String planName);

    boolean existsByPlanName(String casual);
}