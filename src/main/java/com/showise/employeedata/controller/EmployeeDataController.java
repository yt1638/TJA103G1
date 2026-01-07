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

        if (result != null && result.hasErrors()) {
            model.addAttribute("employeeDataVO", employeeDataVO);
            return renderAdminLayout(
                    model,
                    "新增員工",
                    "back-end/employee_data/addEmployeeData :: content"
            );
        }

        empSvc.addEmp(employeeDataVO);
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

        if (result != null && result.hasErrors()) {
            model.addAttribute("employeeDataVO", employeeDataVO);
            return renderAdminLayout(
                    model,
                    "修改員工",
                    "back-end/employee_data/update_employee_data_input :: content"
            );
        }

        empSvc.updateEmp(employeeDataVO);

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
