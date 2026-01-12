package com.showise.memberclass.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.memberclass.model.MemberClassService;
import com.showise.memberclass.model.MemberClassVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/memberClass")
public class MemberClassController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	MemberClassService memberClassService;
	
	@GetMapping("/select_page")
	public String select_page(ModelMap model) {
		List<MemberClassVO> list = memberClassService.getAll();
	    model.addAttribute("memberClassListData", list);
	    model.addAttribute("pageTitle","會員等級管理");
		model.addAttribute("content","back-end/memberClass/select_page :: content");
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("addMemberClass")
	public String addMemberClass(ModelMap model) {
		MemberClassVO memberClassVO = new MemberClassVO();
		model.addAttribute("memberClassVO", memberClassVO);
		model.addAttribute("pageTitle","會員等級管理");
		model.addAttribute("content","back-end/memberClass/addMemberClass :: content");
		return "back-end/layout/admin-layout";
	}
	
	@PostMapping("insert")
	public String insert(@Valid MemberClassVO memberClassVO, BindingResult result, ModelMap model) {
		
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		if(result.hasErrors()) {
			model.addAttribute("pageTitle","會員等級管理");
			model.addAttribute("content","back-end/memberClass/addMemberClass :: content");
			return "back-end/layout/admin-layout";
		}
		
		//******************2.開始新增資料********************************
		memberClassService.addMemberClass(memberClassVO);
		
		//******************3.新增完成，準備轉交****************************	
		List<MemberClassVO> list = memberClassService.getAll();
		model.addAttribute("memberClassListData", list);
		model.addAttribute("success", "-(新增成功)");
		model.addAttribute("pageTitle","新增會員等級");
		model.addAttribute("content","back-end/memberClass/select_page :: content");
		return "back-end/layout/admin-layout";
		
	}
	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("memberClassId") String memberClassId, ModelMap model) {
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始查詢資料********************************
		MemberClassVO memberClassVO = memberClassService.getOneMemberClass(Integer.valueOf(memberClassId));
		
		//******************3.查詢完成，準備轉交****************************
		model.addAttribute("memberClassVO", memberClassVO);
		model.addAttribute("pageTitle","新增會員等級");
		model.addAttribute("content","back-end/memberClass/update_memberClass_insert :: content");
		return "back-end/layout/admin-layout";
	}
	
	@PostMapping("update")
	public String update(@Valid MemberClassVO memberClassVO, BindingResult result, ModelMap model) {
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		if(result.hasErrors()) {
			model.addAttribute("pageTitle","新增會員等級");
			model.addAttribute("content","back-end/memberClass/update_memberClass_insert :: content");
			return "back-end/layout/admin-layout";
		}
		
		//******************2.開始修改資料********************************	
		memberClassService.updateMemberClass(memberClassVO);
		
		//******************3.修改完成，準備轉交****************************
		model.addAttribute("success", "-(修改成功)");
		memberClassVO = memberClassService.getOneMemberClass(Integer.valueOf(memberClassVO.getMemberClassId()));
		model.addAttribute("memberClassVO", memberClassVO);
		model.addAttribute("pageTitle","會員等級管理");
		model.addAttribute("content","back-end/memberClass/select_page :: content");
		return "back-end/layout/admin-layout";
	}
	
	@PostMapping("delete")
	public String delete(@RequestParam("memberClassId") String memberClassId, ModelMap model) {
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始刪除資料********************************
		memberClassService.deleteMemberClass(Integer.valueOf(memberClassId));
		
		//******************3.刪除完成，準備轉交****************************	
		List<MemberClassVO> list = memberClassService.getAll();
		model.addAttribute("memberClassListData", list);
		model.addAttribute("success", "-(刪除成功)");
		return "back-end/layout/admin-layout";	//這邊用redirect，避免重複送出
	}
	
	// 會員
	@ModelAttribute("memberListData")
	protected List<MemberVO> referenceListData_Member(){
		List<MemberVO> list = memberService.getAll();
		return list;
	}
	
	// 會員等級
	@ModelAttribute("memberClassListData")
	protected List<MemberClassVO> referenceListData(){
		List<MemberClassVO> list = memberClassService.getAll();
		return list;
	}
	
}
