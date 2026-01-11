package com.showise.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.showise.back.interceptor.EmployeePermissionInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final EmployeePermissionInterceptor employeePermissionInterceptor;

    public WebMvcConfig(EmployeePermissionInterceptor employeePermissionInterceptor) {
        this.employeePermissionInterceptor = employeePermissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(employeePermissionInterceptor)
                .addPathPatterns("/employee_data/**")

                .excludePathPatterns(
                        "/admin/login",
                        "/admin/logout",
                        "/css/**", "/js/**", "/img/**", "/vendor/**",
                        "/error"
                );
    }
}
