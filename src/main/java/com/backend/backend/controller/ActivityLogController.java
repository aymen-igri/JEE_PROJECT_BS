package com.backend.backend.controller;

import com.backend.backend.service.ActivityLog.ActivityLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activityLog")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllActivityLogs() {
        return ResponseEntity.status(HttpStatus.OK).body(activityLogService.getAllActivityLogs());
    }
}
