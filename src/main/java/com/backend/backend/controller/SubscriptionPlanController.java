package com.backend.backend.controller;

import com.backend.backend.dto.request.subscription.SubscriptionPlanRequest;
import com.backend.backend.dto.request.subscription.UpdateSPRequest;
import com.backend.backend.service.subscription.SubscriptionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody SubscriptionPlanRequest request
    ) throws Exception{
        return ResponseEntity.ok(subscriptionPlanService.addNewSP(request));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateSP(
            @RequestBody UpdateSPRequest request
    ) throws Exception{
        return ResponseEntity.ok(subscriptionPlanService.updateSP(request));
    }
}
