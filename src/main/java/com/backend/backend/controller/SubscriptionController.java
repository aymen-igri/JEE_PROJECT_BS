package com.backend.backend.controller;

import com.backend.backend.dto.request.subscription.CreateSubscriptionRequest;
import com.backend.backend.dto.response.Subscription.SubscriptionResponse;
import com.backend.backend.entity.subscription.Subscription;
import com.backend.backend.mapper.Subscription.SubscriptionMapper;
import com.backend.backend.service.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper subscriptionMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubscriptions(){
        return ResponseEntity.ok(subscriptionService.getAllSubscription());
    }

    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @RequestBody CreateSubscriptionRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Subscription subscription = subscriptionService.createSubscriptionForUser(
                userEmail,
                String.valueOf(request.getPlanId()),
                request.getAutoRenew()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionMapper.toSubscriptionResponse(subscription));
    }

    @PutMapping("/{subscriptionId}/upgrade")
    public ResponseEntity<SubscriptionResponse> upgradeSubscription(
            @PathVariable UUID subscriptionId,
            @RequestParam UUID newPlanId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Subscription subscription = subscriptionService.upgradeSubscription(
                subscriptionId,
                newPlanId,
                userEmail
        );

        return ResponseEntity.ok(subscriptionMapper.toSubscriptionResponse(subscription));
    }

    @PutMapping("/{subscriptionId}/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @PathVariable UUID subscriptionId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Subscription subscription = subscriptionService.cancelSubscription(
                subscriptionId,
                userEmail
        );

        return ResponseEntity.ok(subscriptionMapper.toSubscriptionResponse(subscription));
    }

    @PutMapping("/{subscriptionId}/renew")
    public ResponseEntity<SubscriptionResponse> renewSubscription(
            @PathVariable UUID subscriptionId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        Subscription subscription = subscriptionService.renewSubscription(
                subscriptionId,
                userEmail
        );

        return ResponseEntity.ok(subscriptionMapper.toSubscriptionResponse(subscription));
    }

    @GetMapping("/my-subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(
            Authentication authentication) {

        String userEmail = authentication.getName();

        Subscription subscription = subscriptionService.getActiveSubscriptionForUser(userEmail);

        return ResponseEntity.ok(subscriptionMapper.toSubscriptionResponse(subscription));
    }

    @GetMapping("/status")
    public ResponseEntity<String> getMySubscriptionStatus(
            Authentication authentication) {

        String userEmail = authentication.getName();
        String status = subscriptionService.getSubscriptionStatusForUser(userEmail);

        return ResponseEntity.ok(status);
    }
}