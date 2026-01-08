package com.backend.backend.dto.response.Statistics;
public record DoctorStatisticsResponse(
        long totalDoctors,
        long activeDoctors,
        long inactiveDoctors,
        double inactiveDoctorsPercentage,
        long pendingApplications,
        long approvedApplications,
        long rejectedApplications
) {}
