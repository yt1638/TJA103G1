package com.showise.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.showise.member.model.AuthCodeService;
import com.showise.member.model.AuthCodeMailService;
import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/loginAndRegister")
@Controller		
public class VerificationController {

	@Autowired
	private AuthCodeService authCodeService;
	
	@Autowired
	private AuthCodeMailService mailService;
	
	@Autowired
	private MemberService memberService;
	
	
	@GetMapping("valid")
	public String validatePage(Model model, HttpSession session) {
	    // 取得暫存資料（如果需要）
	    MemberVO tempMember = (MemberVO) session.getAttribute("tempMember");
	    if (tempMember == null) {
	        return "redirect:/loginAndRegister/register"; // 如果 session 失效，回註冊頁
	    }
	    model.addAttribute("email", tempMember.getEmail());
	    return "front-end/loginAndRegister/valid"; // 這個對應你的驗證碼 HTML
	}

	
	// 重新寄送驗證碼至使用者信箱
	@PostMapping("/send")
	public String sendAuthCode(HttpSession session, Model model) {
		
	    String tempMemberId = (String) session.getAttribute("tempMemberId");
	    MemberVO tempMember = (MemberVO)session.getAttribute("tempMember");
		
	    if(tempMemberId == null || tempMember == null) {
	    	model.addAttribute("errorMessage", "註冊資料不存在，請重新註冊");
	        return "redirect:/loginAndRegister/register";
	    }
	    
		String authCode = authCodeService.generateAndSave(tempMemberId);
		mailService.sendAuthCodeMail(tempMember.getEmail(), authCode);
		
		model.addAttribute("successMessage", "驗證碼已發送至您的信箱");
		return "front-end/loginAndRegister/valid";
	}
	
	// 檢查驗證碼
	@PostMapping("/check")
	public String checkAuthCode(
			@RequestParam String authCode,
			HttpSession session,
			Model model) {
		
		String tempMemberId = (String) session.getAttribute("tempMemberId");
		MemberVO tempMember = (MemberVO)session.getAttribute("tempMember");
		
		
		if(tempMemberId == null || tempMember == null) {
			model.addAttribute("errorMessage", "找不到註冊會員資訊，請重新再試一次");
	        return "redirect:/loginAndRegister/register";
	    }
		
		if(authCodeService.verify(tempMemberId, authCode)) {
			
			// 驗證成功，將資料存進資料庫
			MemberVO savedMember = memberService.register(tempMember);
			
			// 清掉暫存資料
			session.removeAttribute("tempMemberId");
			session.removeAttribute("tempMember");
			
			session.setAttribute("loginMember", savedMember);
			
			return "redirect:/loginAndRegister/registerStyle";
		}
		
		model.addAttribute("errorMessage", "驗證失敗，請再試一次");
		return "front-end/loginAndRegister/valid";
	}
	
	
}
