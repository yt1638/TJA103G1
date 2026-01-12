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
                // ✅ 只攔「員工管理」相關路徑（你要擋的功能都加在這）
                .addPathPatterns("/employee_data/**")

                // ✅ (可選) 這些不擋（通常不需要，但你可保留）
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/logout",
                        "/css/**", "/js/**", "/img/**", "/vendor/**",
                        "/error"
                );
    }
}
