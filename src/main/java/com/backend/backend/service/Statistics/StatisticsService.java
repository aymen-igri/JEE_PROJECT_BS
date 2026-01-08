package com.backend.backend.service.Statistics;
import com.backend.backend.dto.response.Statistics.DoctorStatisticsResponse;
import com.backend.backend.dto.response.Statistics.SecretaryStatisticsResponse;
import com.backend.backend.service.Doctor.DoctorAppService;
import com.backend.backend.service.Doctor.DoctorService;
import com.backend.backend.service.Secretary.SecretaryService;
import org.springframework.stereotype.Service;
@Service
public class StatisticsService {
    private final DoctorService doctorService;
    private final DoctorAppService doctorAppService;
    private final SecretaryService secretaryService;
    public StatisticsService(
            DoctorService doctorService, 
            DoctorAppService doctorAppService,
            SecretaryService secretaryService
    ) {
        this.doctorService = doctorService;
        this.doctorAppService = doctorAppService;
        this.secretaryService = secretaryService;
    }
    public DoctorStatisticsResponse getDoctorStatistics() {
        return new DoctorStatisticsResponse(
                doctorService.getTotalDoctorsCount(),
                doctorService.getActiveDoctorsCount(),
                doctorService.getInactiveDoctorsCount(),
                doctorService.getInactiveDoctorsPercentage(),
                doctorAppService.getPendingApplicationsCount(),
                doctorAppService.getApprovedApplicationsCount(),
                doctorAppService.getRejectedApplicationsCount()
        );
    }
    public SecretaryStatisticsResponse getSecretaryStatistics() {
        return new SecretaryStatisticsResponse(
                secretaryService.getTotalSecretariesCount(),
                secretaryService.getActiveSecretariesCount(),
                secretaryService.getInactiveSecretariesCount()
        );
    }
}
