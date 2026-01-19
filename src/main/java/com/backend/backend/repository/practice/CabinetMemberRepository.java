package com.backend.backend.repository.practice;

import com.backend.backend.entity.practice.CabinetMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for CabinetMember entity.
 * Used to find which cabinet a user belongs to.
 */
@Repository
public interface CabinetMemberRepository extends JpaRepository<CabinetMember, UUID> {

    /**
     * Find active cabinet membership for a user.
     * Active = leftDate is null (hasn't left the cabinet).
     */
    @Query("SELECT cm FROM CabinetMember cm " +
           "JOIN FETCH cm.cabinet " +
           "WHERE cm.user.userId = :userId " +
           "AND cm.leftDate IS NULL")
    Optional<CabinetMember> findActiveByUserId(@Param("userId") UUID userId);

    /**
     * Get cabinet ID for a user.
     */
    @Query("SELECT cm.cabinet.cabinetId FROM CabinetMember cm " +
           "WHERE cm.user.userId = :userId " +
           "AND cm.leftDate IS NULL")
    Optional<UUID> findCabinetIdByUserId(@Param("userId") UUID userId);

    /**
     * Check if user is a member of a specific cabinet.
     */
    @Query("SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END " +
           "FROM CabinetMember cm " +
           "WHERE cm.user.userId = :userId " +
           "AND cm.cabinet.cabinetId = :cabinetId " +
           "AND cm.leftDate IS NULL")
    boolean isActiveMember(@Param("userId") UUID userId, @Param("cabinetId") UUID cabinetId);
}

