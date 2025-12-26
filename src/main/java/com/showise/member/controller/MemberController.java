package com.showise.member.controller;

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
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	MemberClassService memberClassService;
	
	
	@GetMapping("addMember")
	public String addMember(ModelMap model) {
		MemberVO member = new MemberVO();
		model.addAttribute("memberVO", member);
		return "back-end/member/addMember";
	}
	
	
	@PostMapping("insert")
	public String insert(@Valid MemberVO member, BindingResult result, ModelMap model) {
		
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		if(result.hasErrors()) {
			return "back-end/member/addMember";
		}
		
		//******************2.開始新增資料********************************
		memberService.addMember(member);
		
		//******************3.新增完成，準備轉交****************************	
		List<MemberVO> list = memberService.getAll();
		model.addAttribute("memberListData", list);
		model.addAttribute("success", "-(新增成功)");
		return "redirect:/member/listAllMember";	// 新增成功後，重導至IndexController.java的@GetMapping("/member/listAllMember")		
	}
	
	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("memberId") String memberId, ModelMap model) {
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始查詢資料********************************
		MemberVO member = memberService.getOneMember(Integer.valueOf(memberId));
		
		//******************3.查詢完成，準備轉交****************************	
		model.addAttribute("memberVO", member);
		return "back-end/member/update_member_input";	// 查詢完成後，轉交給update_member_input.html
	}
	
	
	@PostMapping("update")
	public String update(@Valid MemberVO member, BindingResult result, ModelMap model) {
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		if(result.hasErrors()) {
			return "back-end/member/update_member_input";
		}
		
		//******************2.開始修改資料********************************	
		memberService.updateMember(member);
		
		//******************3.修改完成，準備轉交****************************
		model.addAttribute("success", "-(修改成功)");
		member = memberService.getOneMember(Integer.valueOf(member.getMemberId()));
		model.addAttribute("memberVO", member);
		return "back-end/member/listOneMember";		// 修改成功後，轉交給listOneMember.html
	}
	
	
	@PostMapping("delete")
	public String delete(@RequestParam("memberId") String memberId, ModelMap model) {
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始查詢資料********************************
		memberService.deleteMember(Integer.valueOf(memberId));
		
		//******************3.查詢完成，準備轉交****************************	
		List<MemberVO> list = memberService.getAll();
		model.addAttribute("memberListData", list);
		model.addAttribute("success", "-(刪除成功)");
		return "back-end/member/listAllMember";		// 刪除完成後，轉交給listAllMember.html
	}
	
	
	@ModelAttribute("memberClassListData")
	protected List<MemberClassVO>  referenceListData(){
		List<MemberClassVO> list = memberClassService.getAll();
		return list;
	}
	
	
	
	//******************1.接收請求參數，輸入格式的錯誤處理	******************
	//******************2.開始查詢資料********************************	
	//******************3.查詢完成，準備轉交****************************	
	
	
	
	
	
	
	
	
	
	
}
