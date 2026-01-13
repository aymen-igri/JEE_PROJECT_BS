package com.backend.backend.dto.response.Doctor;

import com.backend.backend.dto.response.ScheduleItemDTO;
import com.backend.backend.dto.response.Consultation.ConsultationCardDTO;

import java.util.List;

public record DoctorDashboardDTO(
        List<ConsultationCardDTO> currentConsultations,
        List<ScheduleItemDTO> schedule
) {}
