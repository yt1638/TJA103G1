package com.showise.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String index(Model model) {
        model.addAttribute("pageTitle", "後台首頁");
        model.addAttribute("content", "back-end/dashboard :: content");
        return "back-end/layout/admin-layout";
    }
}
