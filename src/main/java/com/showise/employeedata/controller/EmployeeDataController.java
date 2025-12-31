package com.showise.employeedata.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.showise.employeedata.model.EmployeeDataService;
import com.showise.employeedata.model.EmployeeDataVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/employee_data")
public class EmployeeDataController {

    @Autowired
    private EmployeeDataService empSvc;

    @GetMapping("/select_page")
    public String selectPage(ModelMap model) {

        List<EmployeeDataVO> list = empSvc.getAll();
        model.addAttribute("employeeDataListData", list);

        if (!model.containsAttribute("employeeDataVO")) {
            model.addAttribute("employeeDataVO", new EmployeeDataVO());
        }

        return "back-end/employee_data/select_page";
    }

    @PostMapping("/getOne_For_Display")
    public String getOne_For_Display(@RequestParam("empId") Integer empId, ModelMap model) {

        EmployeeDataVO employeeDataVO = empSvc.getOneEmp(empId);
        model.addAttribute("employeeDataVO", employeeDataVO);

        List<EmployeeDataVO> list = empSvc.getAll();
        model.addAttribute("employeeDataListData", list);

        return "back-end/employee_data/select_page";
    }

    @GetMapping("/addEmployeeData")
    public String addEmp(ModelMap model) {
        EmployeeDataVO empVO = new EmployeeDataVO();
        model.addAttribute("employeeDataVO", empVO);
        return "back-end/employee_data/addEmployeeData";
    }

    @PostMapping("/insert")
    public String insert(@Valid EmployeeDataVO empVO,
                         BindingResult result,
                         ModelMap model,
                         @RequestParam("upFiles") MultipartFile[] parts) throws IOException {

        result = removeFieldError(empVO, result, "upFiles");

        if (result != null && result.hasErrors()) {
            return "back-end/employee_data/addEmployeeData";
        }

        empSvc.addEmp(empVO);

        return "redirect:/employee_data/listAllEmployeeData";
    }

    private BindingResult removeFieldError(@Valid EmployeeDataVO empVO,
                                           BindingResult result,
                                           String fieldName) {
        return result;
    }

    /** 進入修改頁 */
    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("empId") String empId, ModelMap model) {
        EmployeeDataVO empVO = empSvc.getOneEmp(Integer.valueOf(empId));
        model.addAttribute("empVO", empVO);
        return "back-end/employee_data/update_employee_data_input";
    }

    @PostMapping("/update")
    public String update(@Valid EmployeeDataVO empVO,
                         BindingResult result,
                         ModelMap model,
                         @RequestParam("upFiles") MultipartFile[] parts) throws IOException {

        result = removeFieldError(empVO, result, "upFiles");

        if (result != null && result.hasErrors()) {
            model.addAttribute("empVO", empVO);
            return "back-end/employee_data/update_employee_data_input";
        }

        empSvc.updateEmp(empVO);

        model.addAttribute("success", "- (修改成功)");
        empVO = empSvc.getOneEmp(empVO.getEmpId());
        model.addAttribute("empVO", empVO);

        return "back-end/employee_data/listOne_employee_data";
    }

    @GetMapping("/listAllEmployeeData")
    public String listAllEmployeeData(ModelMap model) {

        List<EmployeeDataVO> list = empSvc.getAll();

        model.addAttribute("employeeDataVOListData", list);

        return "back-end/employee_data/listAllEmployeeData";
    }


}
