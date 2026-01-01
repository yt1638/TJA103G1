package com.showise.employeedata.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.showise.employeedata.model.EmployeeDataService;
import com.showise.employeedata.model.EmployeeDataVO;

import jakarta.servlet.http.HttpServletRequest;
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
        EmployeeDataVO employeeDataVO = new EmployeeDataVO();
        model.addAttribute("employeeDataVO", employeeDataVO);
        return "back-end/employee_data/addEmployeeData";
    }

    @PostMapping("/insert")
    public String insert(@Valid EmployeeDataVO employeeDataVO,
                         BindingResult result,
                         ModelMap model) {

        if (result != null && result.hasErrors()) {
            model.addAttribute("employeeDataVO", employeeDataVO);
            return "back-end/employee_data/addEmployeeData";
        }

        empSvc.addEmp(employeeDataVO);

        return "redirect:/employee_data/listAllEmployeeData";
    }

    @PostMapping("/update")
    public String update(@Valid EmployeeDataVO employeeDataVO,
                         BindingResult result,
                         ModelMap model) {

        if (result != null && result.hasErrors()) {
            model.addAttribute("employeeDataVO", employeeDataVO);
            return "back-end/employee_data/update_employee_data_input";
        }

        empSvc.updateEmp(employeeDataVO);

        model.addAttribute("success", "- (修改成功)");
        employeeDataVO = empSvc.getOneEmp(employeeDataVO.getEmpId());
        model.addAttribute("employeeDataVO", employeeDataVO);

        return "back-end/employee_data/listOne_employee_data";
    }


    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("empId") String empId, ModelMap model) {
        EmployeeDataVO employeeDataVO = empSvc.getOneEmp(Integer.valueOf(empId));
        model.addAttribute("employeeDataVO", employeeDataVO);
        return "back-end/employee_data/update_employee_data_input";
    }
      

    @GetMapping("/listAllEmployeeData")
    public String listAllEmployeeData(ModelMap model) {

        List<EmployeeDataVO> list = empSvc.getAll();

        model.addAttribute("employeeDataVOListData", list);

        return "back-end/employee_data/listAllEmployeeData";
    }
    
    @PostMapping("listEmployeeDatas_ByCompositeQuery")
	public String listAllEmployeeData(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<EmployeeDataVO> list = empSvc.getAll(map);
		model.addAttribute("employeeDataVOListData", list); 
		return "back-end/employee_data/listAllEmployeeData";
	}


}
