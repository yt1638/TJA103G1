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

        // 1) 沒 session 或沒登入：擋
        if (session == null || session.getAttribute("loginEmployee") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }

        // 2) 權限判斷：empPerm 必須是 1
        Object permObj = session.getAttribute("empPerm");
        int perm = 0;

        if (permObj instanceof Number) {
            perm = ((Number) permObj).intValue();
        } else if (permObj != null) {
            try {
                perm = Integer.parseInt(permObj.toString());
            } catch (NumberFormatException ignore) {
                perm = 0;
            }
        }

        if (perm != 1) {
            // ✅ 直接擋掉：導回首頁（或改成 /no-permission）
            response.sendRedirect(request.getContextPath() + "/index");
            return false;
        }

        return true;
    }
}
