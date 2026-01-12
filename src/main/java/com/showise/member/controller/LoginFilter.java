package com.showise.member.controller;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginFilter implements Filter{
	
	@Override 
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
					throws IOException, ServletException{
		
		// 因為getSession()和sendRedirect()是HTTP 專用方法，因此需要先將 ServletRequest/ServletResponse 轉型成 HttpServletRequest/HttpServletResponse 
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		// 不新建seesion，如果沒有session回傳null(避免每次未登入的人都產生一個新的session，以節省資源)
		HttpSession session = req.getSession(false);
		
		// 判斷是否已登入
		boolean loggedIn = (session != null && session.getAttribute("loginMember") != null);
		
		if (!loggedIn) {
			res.sendRedirect("/loginAndRegister/memberLogin");
			return;
		}
		
		chain.doFilter(request, response);
	}
}
