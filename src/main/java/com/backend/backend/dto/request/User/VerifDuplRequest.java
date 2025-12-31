package com.backend.backend.dto.request.User;

import jakarta.validation.constraints.NotBlank;

public record VerifDuplRequest(
        @NotBlank(message = "username is required")
        String username,
        @NotBlank(message = "username is required")
        String email
) {
}
