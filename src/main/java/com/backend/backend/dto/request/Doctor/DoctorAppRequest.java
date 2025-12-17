package com.backend.backend.dto.request.Doctor;

import com.backend.backend.dto.request.Auth.AuthRequest;
import com.backend.backend.dto.request.Secretary.SecretaryRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DoctorAppRequest(
        @NotNull @Valid DoctorAppDataRequest secretaryInfo,
        @NotNull @Valid AuthRequest credentials
) {
}
