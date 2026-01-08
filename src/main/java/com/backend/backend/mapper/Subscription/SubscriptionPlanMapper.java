package com.backend.backend.mapper.Subscription;

import com.backend.backend.dto.request.subscription.SubscriptionPlanRequest;
import com.backend.backend.dto.request.subscription.UpdateSPRequest;
import com.backend.backend.dto.response.Subscription.SubscriptionPlanResponse;
import com.backend.backend.dto.response.Subscription.UpdateSPResponse;
import com.backend.backend.entity.subscription.SubscriptionPlan;
import com.backend.backend.repository.subscription.SubscriptionPlanRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SubscriptionPlanMapper {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanMapper(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    public SubscriptionPlan toSP(SubscriptionPlanRequest request) {
        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setPlanName(request.planName());
        subscriptionPlan.setPrice(request.price());
        // createdBy is set by the service from the authenticated user
        subscriptionPlan.setBillingCycle(request.billingCycle());
        subscriptionPlan.setMaxDoctors(request.maxDoctors());
        subscriptionPlan.setMaxSecretary(request.maxSecretary());
        subscriptionPlan.setFeatures(request.features());

        return subscriptionPlan;
    }

    public SubscriptionPlan toSPUpdate(UpdateSPRequest request) {

        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("Subscription Plan with ID " + request.id() + " does not exist."));

        // Update only the fields that should be modified
        subscriptionPlan.setPlanName(request.planName());
        subscriptionPlan.setPrice(request.price());
        subscriptionPlan.setBillingCycle(request.billingCycle());
        subscriptionPlan.setMaxDoctors(request.maxDoctors());
        subscriptionPlan.setMaxSecretary(request.maxSecretary());
        subscriptionPlan.setFeatures(request.features());
        subscriptionPlan.setIsActive(request.isActive());
        subscriptionPlan.setUpdatedAt(LocalDateTime.now());
        // createdBy and createdAt remain unchanged from the original

        return subscriptionPlan;
    }

    public SubscriptionPlanResponse toSPDTO(SubscriptionPlan subscriptionPlan) {
        return new SubscriptionPlanResponse(
                subscriptionPlan.getPlanId(),
                subscriptionPlan.getPlanName(),
                subscriptionPlan.getCreatedBy(),
                subscriptionPlan.getPrice(),
                subscriptionPlan.getBillingCycle(),
                subscriptionPlan.getMaxDoctors(),
                subscriptionPlan.getMaxSecretary(),
                subscriptionPlan.getFeatures(),
                subscriptionPlan.getIsActive(),
                subscriptionPlan.getCreatedAt(),
                subscriptionPlan.getUpdatedAt()
        );
    }

    public UpdateSPResponse toUpdateSPDTO(SubscriptionPlan subscriptionPlan) {
        return new UpdateSPResponse(
                subscriptionPlan.getPlanId(),
                subscriptionPlan.getPlanName(),
                subscriptionPlan.getPrice(),
                subscriptionPlan.getCreatedBy(),
                subscriptionPlan.getBillingCycle(),
                subscriptionPlan.getMaxDoctors(),
                subscriptionPlan.getMaxSecretary(),
                subscriptionPlan.getFeatures(),
                subscriptionPlan.getIsActive(),
                subscriptionPlan.getCreatedAt(),
                subscriptionPlan.getUpdatedAt()
        );
    }
}
