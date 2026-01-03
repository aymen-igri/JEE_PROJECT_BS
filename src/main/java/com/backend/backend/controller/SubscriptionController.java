package com.backend.backend.controller;


import com.backend.backend.dto.request.subscription.CreateSubscriptionRequest;
import com.backend.backend.entity.subscription.Subscription;

import com.backend.backend.service.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(
            @RequestBody CreateSubscriptionRequest request) {

        Subscription subscription = subscriptionService.createSubscriptionWithPayment(
                request.getCabinetId(),
                request.getPlanId(),
                request.getAutoRenew()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @PutMapping("/{subscriptionId}/upgrade")
    public ResponseEntity<Subscription> upgradeSubscription(
            @PathVariable UUID subscriptionId,
            @RequestParam UUID newPlanId) {

        Subscription subscription = subscriptionService.upgradeSubscription(
                subscriptionId,
                newPlanId
        );

        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<Subscription> cancelSubscription(
            @PathVariable UUID subscriptionId,
            @RequestParam Integer cancelledBy) {

        Subscription subscription = subscriptionService.cancelSubscription(
                subscriptionId,
                cancelledBy
        );

        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/{subscriptionId}/renew")
    public ResponseEntity<Subscription> renewSubscription(
            @PathVariable UUID subscriptionId) {

        Subscription subscription = subscriptionService.renewSubscription(subscriptionId);

        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/cabinet/{cabinetId}/active")
    public ResponseEntity<Boolean> checkActiveSubscription(
            @PathVariable UUID cabinetId) {

        boolean isActive = subscriptionService.isSubscriptionActive(cabinetId);

        return ResponseEntity.ok(isActive);
    }

    @GetMapping("/cabinet/{cabinetId}/has-subscribed")
    public ResponseEntity<Boolean> hasEverSubscribed(
            @PathVariable UUID cabinetId) {

        boolean hasSubscribed = subscriptionService.hasEverSubscribed(cabinetId);

        return ResponseEntity.ok(hasSubscribed);
    }

    @GetMapping("/cabinet/{cabinetId}/needs-subscription")
    public ResponseEntity<Boolean> needsSubscription(
            @PathVariable UUID cabinetId) {

        boolean needs = subscriptionService.needsSubscription(cabinetId);

        return ResponseEntity.ok(needs);
    }

    @GetMapping("/cabinet/{cabinetId}/status")
    public ResponseEntity<String> getSubscriptionStatus(
            @PathVariable UUID cabinetId) {

        String status = subscriptionService.getSubscriptionStatus(cabinetId);

        return ResponseEntity.ok(status);
    }
}