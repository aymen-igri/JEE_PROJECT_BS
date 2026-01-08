package com.backend.backend.config;

import com.backend.backend.interceptors.CabinetRequiredInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CabinetRequiredInterceptor cabinetRequiredInterceptor;

    public WebConfig(CabinetRequiredInterceptor cabinetRequiredInterceptor) {
        this.cabinetRequiredInterceptor = cabinetRequiredInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cabinetRequiredInterceptor)
                .addPathPatterns("/dashboard/**", "/patients/**", "/api/dashboard/**", "/api/patients/**")
                .excludePathPatterns("/cabinet/**", "/api/cabinet/**", "/auth/**", "/login", "/register");
    }
}