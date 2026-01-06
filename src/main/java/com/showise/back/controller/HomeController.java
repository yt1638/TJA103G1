package com.showise.back.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/index")
    public String index(Model model) {
    	model.addAttribute("pageTitle","後台首頁");
    	model.addAttribute("content","back-end/index2 :: content");
        return "back-end/layout/admin-layout"; 
    }
}
