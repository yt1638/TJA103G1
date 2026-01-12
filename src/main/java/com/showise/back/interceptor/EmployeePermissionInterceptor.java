package com.showise.back.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class EmployeePermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loginEmployee") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login"); // ✅ 對齊你的 mapping
            return false;
        }

        Object permObj = session.getAttribute("empPerm");
        int perm = 0;

        if (permObj instanceof Number) {
            perm = ((Number) permObj).intValue();
        } else if (permObj != null) {
            try {
                perm = Integer.parseInt(permObj.toString());
            } catch (NumberFormatException ignore) {}
        }

        if (perm != 1) {
            response.sendRedirect(request.getContextPath() + "/index");
            return false;
        }

        return true;
    }
}
