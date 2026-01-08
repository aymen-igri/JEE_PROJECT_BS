package com.backend.backend.dto.response.Statistics;
public record SecretaryStatisticsResponse(
        long totalSecretaries,
        long activeSecretaries,
        long inactiveSecretaries
) {}
