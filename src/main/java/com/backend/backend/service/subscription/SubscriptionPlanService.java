package com.backend.backend.service.subscription;

import com.backend.backend.dto.request.subscription.SubscriptionPlanRequest;
import com.backend.backend.dto.request.subscription.UpdateSPRequest;
import com.backend.backend.dto.response.Subscription.SubscriptionPlanResponse;
import com.backend.backend.dto.response.Subscription.UpdateSPResponse;
import com.backend.backend.entity.subscription.SubscriptionPlan;
import com.backend.backend.mapper.Subscription.SubscriptionPlanMapper;
import com.backend.backend.repository.subscription.SubscriptionPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionPlanService {

    public final SubscriptionPlanRepository subscriptionPlanRepository;
    public final SubscriptionPlanMapper subscriptionPlanMapper;

    public SubscriptionPlanService(
            SubscriptionPlanRepository subscriptionPlanRepository,
            SubscriptionPlanMapper subscriptionPlanMapper
    ) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionPlanMapper = subscriptionPlanMapper;
    }

    @Transactional
    public List<SubscriptionPlanResponse> getAllSPs(){
        return subscriptionPlanRepository.findAll().stream()
                .map(subscriptionPlanMapper::toSPDTO).toList();
    }

    @Transactional
    public SubscriptionPlanResponse addNewSP(SubscriptionPlanRequest planRequest, UUID createdBy) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanMapper.toSP(planRequest);
        subscriptionPlan.setCreatedBy(createdBy);
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(subscriptionPlan);
        return subscriptionPlanMapper.toSPDTO(savedPlan);
    }

    @Transactional
    public UpdateSPResponse updateSP(UpdateSPRequest updateSPRequest) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanMapper.toSPUpdate(updateSPRequest);
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(subscriptionPlan);
        return subscriptionPlanMapper.toUpdateSPDTO(updatedPlan);
    }
}
