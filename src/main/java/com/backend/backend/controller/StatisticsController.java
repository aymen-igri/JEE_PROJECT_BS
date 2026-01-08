package com.backend.backend.controller;
import com.backend.backend.dto.response.Statistics.DoctorStatisticsResponse;
import com.backend.backend.dto.response.Statistics.SecretaryStatisticsResponse;
import com.backend.backend.service.Statistics.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:3000")
public class StatisticsController {
    private final StatisticsService statisticsService;
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/doctors")
    public ResponseEntity<DoctorStatisticsResponse> getDoctorStatistics() {
        DoctorStatisticsResponse statistics = statisticsService.getDoctorStatistics();
        return ResponseEntity.status(HttpStatus.OK).body(statistics);
    }
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @GetMapping("/secretaries")
    public ResponseEntity<SecretaryStatisticsResponse> getSecretaryStatistics() {
        SecretaryStatisticsResponse statistics = statisticsService.getSecretaryStatistics();
        return ResponseEntity.status(HttpStatus.OK).body(statistics);
    }
}
