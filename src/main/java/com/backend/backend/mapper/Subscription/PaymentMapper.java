package com.backend.backend.mapper.Subscription;

import com.backend.backend.dto.response.Subscription.PaymentResponse;
import com.backend.backend.entity.subscription.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    private final SubscriptionMapper subscriptionMapper;

    public PaymentMapper(SubscriptionMapper subscriptionMapper) {
        this.subscriptionMapper = subscriptionMapper;
    }

    public PaymentResponse toPaymentDTO(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                subscriptionMapper.toSubscriptionResponse(payment.getSubscription()),
                payment.getAmount(),
                payment.getPaymentType(),
                payment.getTransactionId(),
                payment.getStatus(),
                payment.getPaidBy(),
                payment.getNotes(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
