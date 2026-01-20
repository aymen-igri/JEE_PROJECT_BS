package com.backend.backend.controller;

import com.backend.backend.dto.request.subscription.SubscriptionPlanRequest;
import com.backend.backend.dto.request.subscription.UpdateSPRequest;
import com.backend.backend.entity.User.User;
import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.subscription.SubscriptionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptionPlan")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSPs(){
        return ResponseEntity.ok(subscriptionPlanService.getAllSPs());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewSP(
            @AuthenticationPrincipal CustomUserDetails comUser,
            @RequestBody SubscriptionPlanRequest request
    ) throws Exception{
        UUID userId = comUser.getUser().getUserId();
        return ResponseEntity.ok(subscriptionPlanService.addNewSP(request, userId));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateSP(
            @RequestBody UpdateSPRequest request
    ) throws Exception{
        return ResponseEntity.ok(subscriptionPlanService.updateSP(request));
    }

    @PatchMapping("/cancel")
    public ResponseEntity<?> cancelSP(
            @RequestBody UpdateSPRequest request
    ) throws Exception{
        return ResponseEntity.ok(subscriptionPlanService.cancelSP(request));
    }
}
