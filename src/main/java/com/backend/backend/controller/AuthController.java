package com.backend.backend.controller;

import com.backend.backend.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getIdentifier(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return new LoginResponse(
                "Login successful",
                userDetails.getUsername(), // This is the email
                authentication.getAuthorities().toString()
        );
    }

    @PostMapping("/logout")
    public LogoutResponse logout() {
        SecurityContextHolder.clearContext();
        return new LogoutResponse("Logout successful");
    }

    @GetMapping("/me")
    public UserInfoResponse getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new UserInfoResponse(
                userDetails.getUsername(),
                userDetails.getAuthorities().toString()
        );
    }
}

// Request/Response DTOs
class LoginRequest {
    private String identifier; // Can be email or username
    private String password;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class LoginResponse {
    private String message;
    private String email;
    private String role;

    public LoginResponse(String message, String email, String role) {
        this.message = message;
        this.email = email;
        this.role = role;
    }

    public String getMessage() { return message; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}

class LogoutResponse {
    private String message;

    public LogoutResponse(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}

class UserInfoResponse {
    private String email;
    private String role;

    public UserInfoResponse(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() { return email; }
    public String getRole() { return role; }
}