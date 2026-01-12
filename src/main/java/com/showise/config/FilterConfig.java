package com.showise.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.showise.member.controller.LoginFilter;

@Configuration
public class FilterConfig {
	
	// Spring啟動時，會建立一個FilterRegistrationBean 物件
	@Bean
	public FilterRegistrationBean<LoginFilter> loginFilter(){
		
		FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new LoginFilter());	//	設定要用哪一個Filter(將該Filter註冊進來)
		bean.addUrlPatterns("/member/*");	//	設定攔截路徑
		bean.setOrder(1);					//	設定Filter執行順序(當有多個Filter時，從數字小的先開始執行)
		return bean;						//	Spring才知道這是一個要啟用的Filter設定
	}
}
