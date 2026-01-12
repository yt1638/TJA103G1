package com.showise.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;

@Configuration
public class JedisConfig {
	
	// 將application.properties裡的設定值注入到變數中
	@Value("${redis.host}")
	private String host;
	
	@Value("${redis.port}")
	private int port;
	
	@Bean
	public JedisPool jedisPool() {
		return new JedisPool(host, port);
	}
}
