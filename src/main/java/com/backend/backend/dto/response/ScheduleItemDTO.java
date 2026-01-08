package com.backend.backend.dto.response;

import java.time.LocalDateTime;

public record ScheduleItemDTO(
        String title,
        String priority,
        String happening,
        String startTime,
        String endTime,
        LocalDateTime appointmentDateTime
) {}