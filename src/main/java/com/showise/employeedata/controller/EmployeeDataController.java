package com.showise.employeedata.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.showise.employeedata.model.EmployeeDataService;
import com.showise.employeedata.model.EmployeeDataVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/employee_data")
public class EmployeeDataController {

	@Autowired
	EmployeeDataService empSvc;



	@GetMapping("addEmployeeData")
	public String addEmp(ModelMap model) {
		EmployeeDataVO empVO = new EmployeeDataVO();
		model.addAttribute("employeeDataVO", empVO);
		return "back-end/employee_data/addEmployeeData";
	}


	@PostMapping("insert")
	public String insert(@Valid EmployeeDataVO empVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

		result = removeFieldError(empVO, result, "upFiles");

		empSvc.addEmp(empVO);
		List<EmployeeDataVO> list = empSvc.getAll();
		model.addAttribute("empListData", list); 
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/employee_data/listAllEmployeeData"; 
	}

	private BindingResult removeFieldError(@Valid EmployeeDataVO empVO, BindingResult result, String string) {
		return null;
	}


	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("empId") String empId, ModelMap model) {
		EmployeeDataVO empVO = empSvc.getOneEmp(Integer.valueOf(empId));

		model.addAttribute("empVO", empVO);
		return "back-end/employee_data/update_employee_data_input"; 
	}

	@PostMapping("update")
	public String update(@Valid EmployeeDataVO empVO, BindingResult result, ModelMap model,
			@RequestParam("upFiles") MultipartFile[] parts) throws IOException {

		result = removeFieldError(empVO, result, "upFiles");

		empSvc.updateEmp(empVO);

		model.addAttribute("success", "- (修改成功)");
		empVO = empSvc.getOneEmp(Integer.valueOf(empVO.getEmpId()));
		model.addAttribute("empVO", empVO);
		return "back-end/employee_data/listOne_employee_data";
	}


}