package com.backend.backend.dto.request.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 5, max = 50 , message = "Username must be between 5 and 50 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$",
                message = "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
        )
        String password

) {
}
