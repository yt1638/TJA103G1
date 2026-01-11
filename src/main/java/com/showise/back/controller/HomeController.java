package com.showise.back.controller;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.employeedata.model.EmployeeDataService;
import com.showise.employeedata.model.EmployeeDataVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private EmployeeDataService employeeService;

    // ✅ 顯示登入頁
    @GetMapping("/admin/login")
    public String loginPage() {
        return "back-end/login"; // 確認對到 templates/back-end/login.html
    }

    // ✅ 登入
    @PostMapping("/admin/login")
    public String login(@RequestParam String account,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {

        EmployeeDataVO emp = employeeService.login(account, password);

        if (emp == null) {
            ra.addFlashAttribute("errorMsg", "帳號或密碼錯誤");
            ra.addFlashAttribute("account", account);
            return "redirect:/admin/login";
        }

        session.setAttribute("loginEmployee", emp);

        // ✅ 安全寫法：這兩個 getter 若你 VO 沒有，也可以同樣用 BeanWrapper 取（但通常都有）
        session.setAttribute("empId", readProp(emp, "empId"));
        session.setAttribute("empName", readProp(emp, "empName"));

        // ✅ 權限：不再呼叫 getEmployeePermissions()，避免你現在的編譯錯誤
        int perm = readPerm(emp);  // 0/1
        session.setAttribute("empPerm", perm);

        return "redirect:/index";
    }

    // ✅ 後台首頁（需登入）
    @GetMapping("/index")
    public String index(Model model, HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("loginEmployee") == null) {
            ra.addFlashAttribute("errorMsg", "請先登入");
            return "redirect:/admin/login";
        }

        model.addAttribute("pageTitle", "後台首頁");
        model.addAttribute("content", "back-end/index2 :: content");
        return "back-end/layout/admin-layout";
    }

    // ✅ 登出
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // =======================
    // helpers
    // =======================

    /** 讀 VO 屬性（不確定 getter 名稱時很有用） */
    private Object readProp(Object bean, String propName) {
        try {
            BeanWrapperImpl bw = new BeanWrapperImpl(bean);
            if (bw.isReadableProperty(propName)) return bw.getPropertyValue(propName);
        } catch (Exception ignore) {}
        return null;
    }

    /** 嘗試從 VO 讀出權限(0/1)，支援多種命名 */
    private int readPerm(EmployeeDataVO emp) {
        String[] candidates = {
                "employeePermissions",
                "empPermissions",
                "employeePermission",
                "empPermission",
                "permissions",
                "permission"
        };

        for (String p : candidates) {
            Object v = readProp(emp, p);
            if (v == null) continue;

            if (v instanceof Number) return ((Number) v).intValue();
            try { return Integer.parseInt(v.toString()); } catch (Exception ignore) {}
        }
        return 0; // 讀不到就當作沒權限
    }
}
