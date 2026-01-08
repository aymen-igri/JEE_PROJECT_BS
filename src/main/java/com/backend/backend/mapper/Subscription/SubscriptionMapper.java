package com.backend.backend.mapper.Subscription;

import com.backend.backend.dto.response.Subscription.SubscriptionResponse;
import com.backend.backend.entity.subscription.Subscription;
import com.backend.backend.mapper.Cabinet.CabinetMapper;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    private final SubscriptionPlanMapper subscriptionPlanMapper;
    private final CabinetMapper cabinetMapper;

    public SubscriptionMapper(
            SubscriptionPlanMapper subscriptionPlanMapper,
            CabinetMapper cabinetMapper
    ) {
        this.subscriptionPlanMapper = subscriptionPlanMapper;
        this.cabinetMapper = cabinetMapper;
    }

    public SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getSubscriptionId(),
                cabinetMapper.toCabinetResponse(subscription.getCabinet()),
                subscriptionPlanMapper.toSPDTO(subscription.getPlan()),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getStatus(),
                subscription.getAutoRenew(),
                subscription.getLastPaymentDate(),
                subscription.getNextPaymentDate(),
                subscription.getGracePeriodEndDate(),
                subscription.getCancelledAt(),
                subscription.getCancelledBy(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
        );
    }
}
