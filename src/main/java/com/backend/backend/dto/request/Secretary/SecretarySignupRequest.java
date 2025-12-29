package com.backend.backend.dto.request.Secretary;

import com.backend.backend.dto.request.Auth.AuthRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SecretarySignupRequest (
        @NotNull @Valid SecretaryRequest secretaryInfo,
        @NotNull @Valid AuthRequest credentials
) {
}
