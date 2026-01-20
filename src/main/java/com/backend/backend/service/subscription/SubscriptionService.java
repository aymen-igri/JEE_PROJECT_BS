package com.backend.backend.service.subscription;

import com.backend.backend.dto.response.Subscription.SubscriptionResponse;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.entity.subscription.Subscription;
import com.backend.backend.entity.subscription.SubscriptionPlan;
import com.backend.backend.entity.User.User;
import com.backend.backend.mapper.Subscription.SubscriptionMapper;
import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.subscription.SubscriptionPlanRepository;
import com.backend.backend.repository.subscription.SubscriptionRepository;
import com.backend.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final CabinetRepository cabinetRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public List<SubscriptionResponse> getAllSubscription(){
        return subscriptionRepository.findAll().stream()
                .map(subscriptionMapper::toSubscriptionResponse).toList();
    }

    // NEW METHOD: Create subscription for authenticated user
    @Transactional
    public Subscription createSubscriptionForUser(String userEmail, String planName, Boolean autoRenew) {
        // 1. Get the user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cabinet cabinet = cabinetRepository.findActiveCabinetByDoctorId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("No active cabinet found for user. Please create an office first."));
        SubscriptionPlan plan = subscriptionPlanRepository.findByPlanName(planName)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found: " + planName));

        // 4. Check if there's already an active subscription
        Optional<Subscription> existingSubscription = subscriptionRepository.findActiveByCabinetId(cabinet.getCabinetId());
        if (existingSubscription.isPresent()) {
            throw new RuntimeException("You already have an active subscription. Please cancel or upgrade your current subscription.");
        }

        if (!plan.getIsActive()) {
            throw new RuntimeException("Selected plan is not active");
        }

        // 6. Create the subscription
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(now, plan.getBillingCycle());
        LocalDate today = LocalDate.now();

        Subscription subscription = Subscription.builder()
                .cabinet(cabinet)
                .plan(plan)
                .startDate(now)
                .endDate(endDate)
                .status("ACTIVE")
                .autoRenew(autoRenew != null ? autoRenew : false)
                .lastPaymentDate(today)
                .nextPaymentDate(endDate.toLocalDate())
                .gracePeriodEndDate(endDate.toLocalDate().plusDays(7))
                .build();

        Subscription saved = subscriptionRepository.save(subscription);

        log.info("Created subscription {} for cabinet {} with plan {}",
                saved.getSubscriptionId(), cabinet.getCabinetId(), plan.getPlanName());

        return saved;
    }

    @Transactional
    public Subscription createSubscription(UUID cabinetId, UUID planId) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        Optional<Cabinet> cabinetOptional = cabinetRepository.findByCabinetId(cabinetId);
        Cabinet cabinet = cabinetOptional.orElseThrow(() -> new RuntimeException("Cabinet not found"));

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
        Cabinet cabinet = cabinetOptional.orElseThrow(() -> new RuntimeException("Cabinet not found"));

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
    public Subscription upgradeSubscription(UUID subscriptionId, UUID newPlanId, String userEmail) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Verify ownership
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!subscription.getCabinet().getDoctor().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You don't have permission to modify this subscription");
        }

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
    public Subscription cancelSubscription(UUID subscriptionId, String userEmail) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Verify ownership
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!subscription.getCabinet().getDoctor().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You don't have permission to cancel this subscription");
        }

        subscription.setStatus("CANCELLED");
        subscription.setCancelledAt(LocalDate.now());
        subscription.setCancelledBy(user.getUserId());
        subscription.setAutoRenew(false);

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription renewSubscription(UUID subscriptionId, String userEmail) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        // Verify ownership
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!subscription.getCabinet().getDoctor().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You don't have permission to renew this subscription");
        }

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

    // NEW METHOD: Get subscription for authenticated user
    public Subscription getActiveSubscriptionForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cabinet cabinet = cabinetRepository.findActiveCabinetByDoctorId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("No active cabinet found"));

        return subscriptionRepository.findActiveByCabinetId(cabinet.getCabinetId())
                .orElseThrow(() -> new RuntimeException("No active subscription found"));
    }

    // NEW METHOD: Get subscription status for authenticated user
    public String getSubscriptionStatusForUser(String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Cabinet cabinet = cabinetRepository.findActiveCabinetByDoctorId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("No active cabinet found"));

            return getSubscriptionStatus(cabinet.getCabinetId());
        } catch (RuntimeException e) {
            return "NONE";
        }
    }

    // EXISTING METHODS BELOW - Keep as is
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