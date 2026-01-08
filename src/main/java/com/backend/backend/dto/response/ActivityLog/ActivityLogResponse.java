package com.backend.backend.dto.response.ActivityLog;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record ActivityLogResponse(
        UUID id,
        String action,
        String entityType,
        String entityId,
        Map<String, Object> details,
        String ipAddress,
        LocalDateTime timestamp,
        Boolean success,
        String errorMessage
) {
}
