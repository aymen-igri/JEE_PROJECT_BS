package com.backend.backend.repository.subscription;

import com.backend.backend.entity.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT s FROM Subscription s WHERE s.cabinet.cabinetId = :cabinetId AND s.status = 'ACTIVE'")
    Optional<Subscription> findActiveByCabinetId(UUID cabinetId);

    @Query("SELECT s FROM Subscription s WHERE s.cabinet.cabinetId = :cabinetId")
    List<Subscription> findByCabinetId(UUID cabinetId);

    // Alternative method name (Spring Data will understand both)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subscription s WHERE s.cabinet.cabinetId = :cabinetId")
    boolean existsByCabinetId(UUID cabinetId);

    // Get most recent subscription for a cabinet
    @Query("SELECT s FROM Subscription s WHERE s.cabinet.cabinetId = :cabinetId ORDER BY s.createdAt DESC LIMIT 1")
    Optional<Subscription> findLatestByCabinetId(UUID cabinetId);

    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate < CURRENT_TIMESTAMP")
    List<Subscription> findExpiredSubscriptions();

    @Query("SELECT s FROM Subscription s WHERE s.autoRenew = true AND s.nextPaymentDate <= CURRENT_DATE AND s.status = 'ACTIVE'")
    List<Subscription> findSubscriptionsDueForRenewal();

    @Query("SELECT s FROM Subscription s WHERE s.gracePeriodEndDate < CURRENT_DATE AND s.status = 'EXPIRED'")
    List<Subscription> findGracePeriodExpired();
}