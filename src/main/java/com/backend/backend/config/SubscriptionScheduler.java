package com.backend.backend.config;

import com.backend.backend.entity.subscription.Subscription;
import com.backend.backend.repository.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * Runs every day at 1 AM to check for expired subscriptions
     * Cron format: second, minute, hour, day, month, weekday
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void checkExpiredSubscriptions() {
        log.info("Starting expired subscription check...");

        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions();

        int count = 0;
        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus("EXPIRED");
            subscriptionRepository.save(subscription);
            count++;

            log.info("Subscription {} for cabinet {} has been marked as EXPIRED",
                    subscription.getSubscriptionId(),
                    subscription.getCabinet().getCabinetId());
        }

        log.info("Expired subscription check completed. Updated {} subscriptions", count);
    }

    /**
     * Runs every hour to check subscriptions that should auto-renew
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void processAutoRenewals() {
        log.info("Starting auto-renewal check...");

        List<Subscription> subscriptionsDueForRenewal =
                subscriptionRepository.findSubscriptionsDueForRenewal();

        int count = 0;
        for (Subscription subscription : subscriptionsDueForRenewal) {
            // Here you would integrate with your payment processor
            // For now, we'll just log it
            log.info("Subscription {} is due for auto-renewal",
                    subscription.getSubscriptionId());

            // TODO: Process payment
            // If payment successful:
            //   - Update subscription dates
            //   - Set lastPaymentDate
            // If payment fails:
            //   - Maybe send notification
            //   - Start grace period

            count++;
        }

        log.info("Auto-renewal check completed. Found {} subscriptions", count);
    }

    /**
     * Runs every day at 2 AM to check grace period expirations
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void checkGracePeriodExpirations() {
        log.info("Starting grace period expiration check...");

        List<Subscription> subscriptionsInGracePeriod =
                subscriptionRepository.findGracePeriodExpired();

        int count = 0;
        for (Subscription subscription : subscriptionsInGracePeriod) {
            subscription.setStatus("SUSPENDED");
            subscriptionRepository.save(subscription);
            count++;

            log.info("Subscription {} has been SUSPENDED after grace period",
                    subscription.getSubscriptionId());
        }

        log.info("Grace period check completed. Suspended {} subscriptions", count);
    }

    /**
     * Alternative: Run every 15 minutes (more frequent checking)
     * Use this if you need near real-time updates
     */
    // @Scheduled(fixedRate = 900000) // 15 minutes in milliseconds
    @Transactional
    public void quickExpirationCheck() {
        List<Subscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions();

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus("EXPIRED");
            subscriptionRepository.save(subscription);
        }
    }
}