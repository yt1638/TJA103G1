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
import com.showise.movie.model.MovieService;
import com.showise.movietype.model.MovieTypeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	MovieService movieSvc;
	@Autowired
	MovieTypeService mtSvc;

    @Autowired
    private EmployeeDataService employeeService;

    @GetMapping("/admin/login")
    public String loginPage() {
        return "back-end/login"; 
    }

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

        Short status = null;
        Object statusObj = readProp(emp, "empStatus"); // 先用常見命名
        if (statusObj == null) statusObj = readProp(emp, "employeeStatus"); // 再試另一種

        if (statusObj instanceof Number) {
            status = ((Number) statusObj).shortValue();
        } else if (statusObj != null) {
            try { status = Short.parseShort(statusObj.toString()); } catch (Exception ignore) {}
        }

        if (status != null && status == 1) {
            ra.addFlashAttribute("errorMsg", "此帳號已停用，不可登入");
            ra.addFlashAttribute("account", account);
            return "redirect:/admin/login";
        }
        session.setAttribute("loginEmployee", emp);

        session.setAttribute("empId", readProp(emp, "empId"));
        session.setAttribute("empName", readProp(emp, "empName"));

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

        model.addAttribute("movieList",movieSvc.findAllOrderByStatusAndMovieId());
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/listAll :: content");
		
		return "back-end/layout/admin-layout";
    }

    // ✅ 登出
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }


    /** 讀 VO 屬性（不確定 getter 命名時很有用） */
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
        return 0;
    }
}