package com.showise.memberclass.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.showise.memberclass.model.MemberClassService;
import com.showise.memberclass.model.MemberClassVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/memberClass")
public class MemberClassIdController {
	
	@Autowired
	MemberClassService memberClassService;
	
	
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
			//******************1.接收請求參數，輸入格式的錯誤處理	******************
			@NotEmpty(message = "會員等級編號: 請勿空白")
			@Digits(integer = 2, fraction = 0, message = "會員等級編號: 請填數字-請勿超過{integer}位數")
			@Min(value = 1, message = "會員等級編號: 不能小於{value}")
			@RequestParam("memberClassId") String memberClassId,
			ModelMap model) {
		
		//******************2.開始查詢資料********************************	
		MemberClassVO memberClassVO = memberClassService.getOneMemberClass(Integer.valueOf(memberClassId));
		
		// 不論查詢成功或失敗，都會回到select_page.html，而這個頁面都需要會員等級清單
		List<MemberClassVO> list = memberClassService.getAll();
		model.addAttribute("memberClassList", list);

		if(memberClassVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/memberClass/select_page";
		}
		
		//******************3.查詢完成，準備轉交****************************	
		model.addAttribute("memberClassVO", memberClassVO);
		return "back-end/memberClass/listOneMemberClass";	// 查詢完成後，轉交listOneMember.html
	}
	
	// 當使用者輸入的資料未通過驗證
		// ExceptionHandler 進行錯誤處理(當這個Controller內發生ConstraintViolationException時，由這個方法負責處理，而不是讓Spring顯示預設錯誤頁面)
	@ExceptionHandler(value = {ConstraintViolationException.class})
	public ModelAndView handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		
		StringBuilder strBuilder = new StringBuilder();
		for(ConstraintViolation<?> violation : violations) {
			strBuilder.append(violation.getMessage() + "<br>");
		}
		
		List<MemberClassVO> list = memberClassService.getAll();
		model.addAttribute("memberClassListData", list);
		String message = strBuilder.toString();
		return new ModelAndView("back-end/memberClass/select_page", "errorMessage", "請修正以下錯誤: <br>" + message);
		
	}
	
}
