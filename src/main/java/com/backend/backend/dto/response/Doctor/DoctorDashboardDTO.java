package com.backend.backend.dto.response.Doctor;

import com.backend.backend.dto.response.ScheduleItemDTO;

import java.util.List;

public record DoctorDashboardDTO(
        List<com.backend.backend.dto.response.Dashboard.ConsultationCardDTO> currentConsultations,
        List<ScheduleItemDTO> schedule
) {}
