package com.backend.backend.interceptors;

import com.backend.backend.security.CustomUserDetails;
import com.backend.backend.service.Doctor.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.UUID;

@Component
public class CabinetRequiredInterceptor implements HandlerInterceptor {

    private final DoctorService doctorService;

    public CabinetRequiredInterceptor(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        @Nullable Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            if (userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {

                UUID doctorId = userDetails.getUserId();

                if (!doctorService.hasCabinet(doctorId)) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json");
                        response.getWriter().write(
                                "{\"error\": \"Cabinet required\", \"redirectUrl\": \"/cabinet/create\"}"
                        );

                    return false;
                }
            }
        }
        return true;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }
}
