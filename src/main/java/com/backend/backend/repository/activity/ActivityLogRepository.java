package com.backend.backend.repository.activity;

import com.backend.backend.entity.activity.ActivityLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;


public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    @Query("SELECT al FROM ActivityLog al " +
            "WHERE al.user.userId = :userId " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLog> findLogsByUserId(@Param("userId") UUID userId);

    @Query("SELECT al FROM ActivityLog al " +
            "WHERE al.entityType = :entityType AND al.entityId = :entityId " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLog> findLogsByEntityTypeAndId(
            @Param("entityType") String entityType,
            @Param("entityId") Integer entityId
    );

    @Query("SELECT al FROM ActivityLog al " +
            "WHERE al.action = :action " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLog> findLogsByAction(@Param("action") String action);

    @Query("SELECT al FROM ActivityLog al " +
            "WHERE al.success = false " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLog> findFailedOperations();

    @Query("SELECT al FROM ActivityLog al " +
            "WHERE al.timestamp BETWEEN :startDate AND :endDate " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLog> findLogsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    @Query("SELECT al FROM ActivityLog al " +
            "WHERE al.user.userId = :userId " +
            "  AND al.timestamp BETWEEN :startDate AND :endDate " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLog> findLogsByUserAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("SELECT al FROM ActivityLog al " +
            "WHERE al.ipAddress = :ipAddress " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLog> findLogsByIpAddress(@Param("ipAddress") String ipAddress);


    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.action = :action")
    long countLogsByAction(@Param("action") String action);


    @Query("SELECT COUNT(al) FROM ActivityLog al " +
            "WHERE al.user.userId = :userId AND al.success = false " +
            "  AND al.timestamp > :since")
    long countFailedOperationsByUserSince(
            @Param("userId") UUID userId,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT al.logId as logId, al.user.username as username, al.action as action, " +
            "       al.timestamp as timestamp, al.success as success " +
            "FROM ActivityLog al " +
            "WHERE al.timestamp > :since " +
            "ORDER BY al.timestamp DESC")
    List<ActivityLogSummaryProjection> findRecentLogsSummary(@Param("since") LocalDateTime since);

}

interface ActivityLogSummaryProjection {
    UUID getLogId();
    String getUsername();
    String getAction();
    LocalDateTime getTimestamp();
    Boolean getSuccess();
}