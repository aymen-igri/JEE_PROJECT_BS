package com.backend.backend.mapper.ActivityLog;

import com.backend.backend.dto.response.ActivityLog.ActivityLogResponse;
import com.backend.backend.entity.activity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toActivityLogResponse(ActivityLog activityLog){
        return  new ActivityLogResponse(
                activityLog.getLogId(),
                activityLog.getAction(),
                activityLog.getEntityType(),
                activityLog.getEntityId(),
                activityLog.getDetails(),
                activityLog.getIpAddress(),
                activityLog.getTimestamp(),
                activityLog.getSuccess(),
                activityLog.getErrorMessage()
        );
    }
}
