package com.backend.backend.dto.request.Admin;

import com.backend.backend.dto.request.Auth.AuthRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AdminSignupRequest(
        @NotNull @Valid AdminRequest AdminInfo,
        @NotNull @Valid AuthRequest credentials
) {
}
