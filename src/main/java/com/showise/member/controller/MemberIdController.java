package com.showise.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.showise.member.model.MemberService;
import com.showise.memberclass.model.MemberClassService;

@Controller
@Validated
@RequestMapping("/member")
public class MemberIdController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	MemberClassService memberClassService;
	
	
//	@PostMapping("getOne_For_Display")
//	public String getOne_For_Display(
//			//******************1.接收請求參數，輸入格式的錯誤處理	******************
//			@NotEmpty(message = "會員編號: 請勿空白")
//			@Digits(integer = 4, fraction = 0, message = "會員編號: 請填數字-請勿超過{integer}位數")
//			@Min(value = 1, message = "會員編號: 不可小於{value}")
//			@RequestParam("memberId") String memberId,
//			ModelMap model)
	
	//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始查詢資料********************************	
		//******************3.查詢完成，準備轉交****************************	
	
	
	
	
	
}
