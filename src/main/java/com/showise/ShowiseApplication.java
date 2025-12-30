package com.showise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class ShowiseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowiseApplication.class, args);
	}
	
	@Controller
	public class LoginController {

	    @GetMapping("/login")          
	    public String showLoginPage() {
	        return "login";           
	    }

	}
}