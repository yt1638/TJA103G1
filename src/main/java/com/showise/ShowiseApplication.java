package com.showise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.showise")
public class ShowiseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowiseApplication.class, args); 
	}
	
}
