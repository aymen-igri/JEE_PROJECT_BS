package com.backend.backend.service.ActivityLog;

import com.backend.backend.dto.response.ActivityLog.ActivityLogResponse;
import com.backend.backend.entity.activity.ActivityLog;
import com.backend.backend.mapper.ActivityLog.ActivityLogMapper;
import com.backend.backend.repository.activity.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository,
            ActivityLogMapper activityLogMapper
    ) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    public List<ActivityLogResponse> getAllActivityLogs() {
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        return activityLogs.stream().map(activityLogMapper::toActivityLogResponse).toList();
    }
}
