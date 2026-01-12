package com.showise.employeedata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.showise.employeedata.model.EmployeeDataService;
import com.showise.employeedata.model.EmployeeDataVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final EmployeeDataService employeeService;

    public LoginController(EmployeeDataService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam("account") String account,
                          @RequestParam("password") String password,
                          Model model,
                          HttpSession session) {

        EmployeeDataVO emp = employeeService.login(account, password);

        // ❌ 帳密錯
        if (emp == null) {
            model.addAttribute("errorMsg", "查無資料，請確認帳號或密碼");
            model.addAttribute("account", account);
            return "login";
        }

        // ⚠️ 帳號未啟用
        if (emp.getEmpStatus() != null && emp.getEmpStatus() == 1) {
            model.addAttribute("errorMsg", "此帳號尚未啟用");
            model.addAttribute("account", account);
            return "login";
        }

        // ✅ 可登入
        session.setAttribute("loginEmp", emp);
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

