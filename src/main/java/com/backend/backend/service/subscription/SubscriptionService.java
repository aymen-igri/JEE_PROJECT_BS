package com.backend.backend.service.subscription;

import com.backend.backend.dto.response.Subscription.SubscriptionResponse;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.entity.subscription.Subscription;
import com.backend.backend.entity.subscription.SubscriptionPlan;
import com.backend.backend.mapper.Subscription.SubscriptionMapper;
import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.subscription.SubscriptionPlanRepository;
import com.backend.backend.repository.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final CabinetRepository cabinetRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public List<SubscriptionResponse> getAllSubscription(){
        return subscriptionRepository.findAll().stream()
                .map(subscriptionMapper::toSubscriptionResponse).toList();
    }

    @Transactional
    public Subscription createSubscription(UUID cabinetId, UUID planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        Optional<Cabinet> cabinetOptional = cabinetRepository.findByCabinetId(cabinetId);
        Cabinet cabinet = cabinetOptional.get();
        if (!plan.getIsActive()) {
            throw new RuntimeException("Selected plan is not active");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(now, plan.getBillingCycle());

        Subscription subscription = new Subscription();
        subscription.setCabinet(cabinet);
        subscription.setPlan(plan);
        subscription.setStartDate(now);
        subscription.setEndDate(endDate);
        subscription.setStatus("ACTIVE");
        subscription.setAutoRenew(false);
        subscription.setNextPaymentDate(endDate.toLocalDate());

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription createSubscriptionWithPayment(UUID cabinetId, UUID planId, boolean autoRenew) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        Optional<Cabinet> cabinetOptional = cabinetRepository.findByCabinetId(cabinetId);
        Cabinet cabinet = cabinetOptional.get();
        if (!plan.getIsActive()) {
            throw new RuntimeException("Selected plan is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(now, plan.getBillingCycle());
        LocalDate today = LocalDate.now();
        Subscription subscription = new Subscription();
        subscription.setCabinet(cabinet);
        subscription.setPlan(plan);
        subscription.setStartDate(now);
        subscription.setEndDate(endDate);
        subscription.setStatus("ACTIVE");
        subscription.setAutoRenew(autoRenew);
        subscription.setNextPaymentDate(endDate.toLocalDate());
        subscription.setLastPaymentDate(today);
        subscription.setGracePeriodEndDate(endDate.toLocalDate().plusDays(7));


        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription upgradeSubscription(UUID subscriptionId, UUID newPlanId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        SubscriptionPlan newPlan = subscriptionPlanRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (!newPlan.getIsActive()) {
            throw new RuntimeException("Selected plan is not active");
        }

        subscription.setPlan(newPlan);
        LocalDateTime newEndDate = calculateEndDate(LocalDateTime.now(), newPlan.getBillingCycle());
        subscription.setEndDate(newEndDate);
        subscription.setNextPaymentDate(newEndDate.toLocalDate());

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription cancelSubscription(UUID subscriptionId, UUID cancelledBy) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setStatus("CANCELLED");
        subscription.setCancelledAt(LocalDate.now());
        subscription.setCancelledBy(cancelledBy);
        subscription.setAutoRenew(false);

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription renewSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(subscription.getPlan().getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        LocalDateTime newStartDate = subscription.getEndDate();
        LocalDateTime newEndDate = calculateEndDate(newStartDate, plan.getBillingCycle());

        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscription.setStatus("ACTIVE");
        subscription.setLastPaymentDate(LocalDate.now());
        subscription.setNextPaymentDate(newEndDate.toLocalDate());

        return subscriptionRepository.save(subscription);
    }

    private LocalDateTime calculateEndDate(LocalDateTime startDate, String billingCycle) {
        return switch (billingCycle.toUpperCase()) {
            case "MONTHLY" -> startDate.plusMonths(1);
            case "QUARTERLY" -> startDate.plusMonths(3);
            case "SEMI_ANNUAL" -> startDate.plusMonths(6);
            case "ANNUAL", "YEARLY" -> startDate.plusYears(1);
            default -> throw new IllegalArgumentException("Invalid billing cycle: " + billingCycle);
        };
    }

    public boolean isSubscriptionActive(UUID cabinetId) {
        return subscriptionRepository.findActiveByCabinetId(cabinetId)
                .map(sub -> "ACTIVE".equals(sub.getStatus()) &&
                        sub.getEndDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
    public boolean hasEverSubscribed(UUID cabinetId) {
        return subscriptionRepository.existsByCabinetId(cabinetId);
    }


    public boolean needsSubscription(UUID cabinetId) {
        return !hasEverSubscribed(cabinetId);
    }

    public String getSubscriptionStatus(UUID cabinetId) {
        return subscriptionRepository.findLatestByCabinetId(cabinetId)
                .map(Subscription::getStatus)
                .orElse("NONE");
    }
}