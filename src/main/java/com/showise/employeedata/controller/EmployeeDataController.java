package com.showise.employeedata.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.employeedata.model.EmployeeDataService;
import com.showise.employeedata.model.EmployeeDataVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/employee_data")
public class EmployeeDataController {

    @Autowired
    private EmployeeDataService empSvc;

    private String renderAdminLayout(Model model, String pageTitle, String contentFragment) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", contentFragment);
        return "back-end/layout/admin-layout";
    }

    @GetMapping("/select_page")
    public String selectPage(Model model) {

        List<EmployeeDataVO> list = empSvc.getAll();
        model.addAttribute("employeeDataListData", list);

        // 右側 detail：預設空物件（避免 Thymeleaf 取值 NPE）
        if (!model.containsAttribute("employeeDataVO")) {
            model.addAttribute("employeeDataVO", new EmployeeDataVO());
        }

        return renderAdminLayout(
                model,
                "員工管理",
                "back-end/employee_data/select_page :: content"
        );
    }

    @PostMapping("/getOne_For_Display")
    public String getOne_For_Display(@RequestParam("empId") Integer empId, Model model) {

        EmployeeDataVO employeeDataVO = empSvc.getOneEmp(empId);
        model.addAttribute("employeeDataVO", employeeDataVO);

        List<EmployeeDataVO> list = empSvc.getAll();
        model.addAttribute("employeeDataListData", list);

        return renderAdminLayout(
                model,
                "員工管理",
                "back-end/employee_data/select_page :: content"
        );
    }

    @GetMapping("/addEmployeeData")
    public String addEmp(Model model) {
        model.addAttribute("employeeDataVO", new EmployeeDataVO());

        return renderAdminLayout(
                model,
                "新增員工",
                "back-end/employee_data/addEmployeeData :: content"
        );
    }

    @PostMapping("/insert")
    public String insert(@Valid EmployeeDataVO employeeDataVO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        // ① 先做「重複檢查」：把錯誤綁回欄位（會對應你 thymeleaf 的 th:errors）
        if (empSvc.existsEmpName(employeeDataVO.getEmpName())) {
            result.rejectValue("empName", "duplicate", "員工姓名已存在，請更換");
        }
        if (empSvc.existsEmpAccount(employeeDataVO.getEmpAccount())) {
            result.rejectValue("empAccount", "duplicate", "員工帳號已存在，請更換");
        }
        if (empSvc.existsEmpPassword(employeeDataVO.getEmpPassword())) {
            result.rejectValue("empPassword", "duplicate", "員工密碼已被使用，請更換");
        }
        if (empSvc.existsEmpEmail(employeeDataVO.getEmpEmail())) {
            result.rejectValue("empEmail", "duplicate", "員工信箱已存在，請更換");
        }

        // ② 任何錯誤（包含 @Valid + duplicate）→ 回新增頁
        if (result.hasErrors()) {
            model.addAttribute("employeeDataVO", employeeDataVO);
            return renderAdminLayout(
                    model,
                    "新增員工",
                    "back-end/employee_data/addEmployeeData :: content"
            );
        }

        // ③ 真正寫入
        try {
            empSvc.addEmp(employeeDataVO);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 資料庫 UNIQUE 最後防線（併發/漏網）
            result.reject("duplicate", "資料已存在（欄位重複），請檢查後再送出");
            model.addAttribute("employeeDataVO", employeeDataVO);
            return renderAdminLayout(
                    model,
                    "新增員工",
                    "back-end/employee_data/addEmployeeData :: content"
            );
        }

        redirectAttributes.addFlashAttribute("success", "- (新增成功)");
        return "redirect:/employee_data/listAllEmployeeData";
    }


    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("empId") Integer empId, Model model) {

        EmployeeDataVO employeeDataVO = empSvc.getOneEmp(empId);
        model.addAttribute("employeeDataVO", employeeDataVO);

        return renderAdminLayout(
                model,
                "修改員工",
                "back-end/employee_data/update_employee_data_input :: content"
        );
    }

    @PostMapping("/update")
    public String update(@Valid EmployeeDataVO employeeDataVO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        Integer empId = employeeDataVO.getEmpId();

        if (empSvc.existsEmpNameExcludeId(employeeDataVO.getEmpName(), empId)) {
            result.rejectValue("empName", "duplicate", "員工姓名已存在，請更換");
        }
        if (empSvc.existsEmpAccountExcludeId(employeeDataVO.getEmpAccount(), empId)) {
            result.rejectValue("empAccount", "duplicate", "員工帳號已存在，請更換");
        }
        if (empSvc.existsEmpPasswordExcludeId(employeeDataVO.getEmpPassword(), empId)) {
            result.rejectValue("empPassword", "duplicate", "員工密碼已被使用，請更換");
        }
        if (empSvc.existsEmpEmailExcludeId(employeeDataVO.getEmpEmail(), empId)) {
            result.rejectValue("empEmail", "duplicate", "員工信箱已存在，請更換");
        }

        if (result.hasErrors()) {
            model.addAttribute("employeeDataVO", employeeDataVO);
            return renderAdminLayout(
                    model,
                    "修改員工",
                    "back-end/employee_data/update_employee_data_input :: content"
            );
        }

        try {
            empSvc.updateEmp(employeeDataVO);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            result.reject("duplicate", "資料已存在（欄位重複），請檢查後再送出");
            model.addAttribute("employeeDataVO", employeeDataVO);
            return renderAdminLayout(
                    model,
                    "修改員工",
                    "back-end/employee_data/update_employee_data_input :: content"
            );
        }

        redirectAttributes.addFlashAttribute("success", "- (修改成功)");
        redirectAttributes.addFlashAttribute("employeeDataVO",
                empSvc.getOneEmp(employeeDataVO.getEmpId()));

        return "redirect:/employee_data/select_page";
    }


    @GetMapping("/listAllEmployeeData")
    public String listAllEmployeeData(Model model) {

        List<EmployeeDataVO> list = empSvc.getAll();
        model.addAttribute("employeeDataVOListData", list);

        return renderAdminLayout(
                model,
                "所有員工資料",
                "back-end/employee_data/listAllEmployeeData :: content"
        );
    }

    @PostMapping("/listEmployeeDatas_ByCompositeQuery")
    public String listEmployeeDatas_ByCompositeQuery(HttpServletRequest req, Model model) {

        String empId = req.getParameter("empId");
        String empName = req.getParameter("empName");
        String empCreateTime = req.getParameter("empCreateTime");

        empId = empId == null ? "" : empId.trim();
        empName = empName == null ? "" : empName.trim();
        empCreateTime = empCreateTime == null ? "" : empCreateTime.trim();

        // ❌ 全空
        if (empId.isEmpty() && empName.isEmpty() && empCreateTime.isEmpty()) {
            model.addAttribute("errorMessage", "複合查詢請至少輸入一個條件");
            return renderAdminLayout(
                    model,
                    "員工管理",
                    "back-end/employee_data/select_page :: content"
            );
        }

        // ❌ 員工編號不是數字
        if (!empId.isEmpty() && !empId.matches("\\d+")) {
            model.addAttribute("errorMessage", "員工編號需填入數字");
            return renderAdminLayout(
                    model,
                    "員工管理",
                    "back-end/employee_data/select_page :: content"
            );
        }

        // ❌ 員工名稱不是英文或英文數字
        if (!empName.isEmpty() && !empName.matches("[A-Za-z0-9]+")) {
            model.addAttribute("errorMessage", "員工名稱需填入英文或英文數字");
            return renderAdminLayout(
                    model,
                    "員工管理",
                    "back-end/employee_data/select_page :: content"
            );
        }

        // ✅ 通過驗證 → 查詢
        Map<String, String[]> map = req.getParameterMap();
        List<EmployeeDataVO> list = empSvc.getAll(map);

        model.addAttribute("employeeDataVOListData", list);

        return renderAdminLayout(
                model,
                "員工管理",
                "back-end/employee_data/listAllEmployeeData :: content"
        );
    }
    
    
    
    

}
