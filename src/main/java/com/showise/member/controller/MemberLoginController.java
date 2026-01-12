package com.showise.member.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.member.model.AuthCodeService;
import com.showise.member.model.AuthCodeMailService;
import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.member.model.PasswordMailService;
import com.showise.member.model.PasswordService;
import com.showise.memberprefertype.model.MemberPreferTypeService;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/loginAndRegister")
@Controller
public class MemberLoginController {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberPreferTypeService memberPreferTypeService;
	
	@Autowired
	private AuthCodeService authCodeService;
	
	@Autowired
	private AuthCodeMailService mailService;
	
	@Autowired
	private PasswordService passwordService;
	
	@Autowired
	private PasswordMailService passwordMailService;
	
	
	@GetMapping("memberLogin")
	public String memberLogin() {
		return "front-end/loginAndRegister/memberLogin";
	}
	
	@GetMapping("memberLogout")
	public String memberLogout(HttpSession session) {
		session.invalidate(); // 清除session
		return "redirect:/loginAndRegister/memberLogin";
	}
	
	@GetMapping("register")
	public String memberRegister(Model model) {
		model.addAttribute("member", new MemberVO());
		return "front-end/loginAndRegister/register";
	}
	
	@GetMapping("registerStyle")
	public String memberRegisterStyle(HttpSession session) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		
		// 若未登入或驗證未完成
		if(loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
		return "front-end/loginAndRegister/registerStyle";
	}
	
	@GetMapping("forgetPwd")
	public String forgetPwd() {
		return "front-end/loginAndRegister/forgetPwd";
	}
	
	@GetMapping("inputForgetPwd")
	public String inputForgetPwd() {
		return "front-end/loginAndRegister/inputForgetPwd";
	}
	
	
	
	@PostMapping("memberLoginHandler")
	public String login(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			HttpSession session,
			Model model) {
		
		MemberVO member = memberService.loginByEmail(email, password);
		
		if(email == null || (email.trim().isEmpty()) ||
			password == null || (password.trim().isEmpty())	) {
			
			model.addAttribute("loginError", "請輸入帳號與密碼");
			return "front-end/loginAndRegister/memberLogin";
		}
		
		if(member == null) {
			model.addAttribute("loginError", "帳號或密碼錯誤");
			return "front-end/loginAndRegister/memberLogin";
		}
		
		session.setAttribute("loginMember", member);
		return "redirect:/member/mainMemberPage";
		
	}
	
	@PostMapping("memberRegisterHandler")
	public String register(
		@ModelAttribute("member") MemberVO member,
		BindingResult result,
		Model model,
		HttpSession session) {
		
		// 表單驗證
		if(result.hasErrors()) {
			model.addAttribute("member", member);
			return "front-end/loginAndRegister/register";
		}
		
		if(memberService.existsByEmail(member.getEmail())) {
			model.addAttribute("errorMsg", "此Email已註冊過，請嘗試其他Email");
			model.addAttribute("member", member);
			return "front-end/loginAndRegister/register";
		}
		
		// 生成暫存MemberID
	    String tempMemberId = java.util.UUID.randomUUID().toString();
	    session.setAttribute("tempMemberId", tempMemberId);
	    session.setAttribute("tempMember", member);
	    
	    // 產生併計送驗證碼
	    String authCode = authCodeService.generateAndSave(tempMemberId);
	    mailService.sendAuthCodeMail(member.getEmail(), authCode);
	    
		return "redirect:/loginAndRegister/valid";		//進入驗證頁面
		
	}
	
	@PostMapping("chooseStyle")
	public String chooseStyle(				// 避免使用者「完全沒勾」，因此出現400
			@RequestParam(value ="styleId", required = false) List<Integer> styleIds,
			HttpSession session) {
		
		MemberVO loginMember = (MemberVO)session.getAttribute("loginMember");
		if (loginMember == null) {
	        return "redirect:/loginAndRegister/memberLogin";
	    }
		
		if(styleIds != null && !styleIds.isEmpty()) {
			memberPreferTypeService.saveMemberPreferTypes(loginMember, styleIds);
		}
		return "redirect:/member/mainMemberPage";
	}
	
	@PostMapping("sendForgetPwd")
	public String sendForgetPwd(@RequestParam("email") String email,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		if(email == null || email.isBlank()){
	        redirectAttributes.addFlashAttribute("emailBlankErrorMessage", "請輸入電子信箱");
	        return "redirect:/loginAndRegister/forgetPwd";
	    }
		
		// 檢查帳號是否註冊過
		if(memberService.existsByEmail(email)) {
			String verifyCode = passwordService.generateAndSave(email);
			passwordMailService.sendPwdMail(email, verifyCode);
			
			session.setAttribute("forgetPwdEmail", email);
		}
		
		// 不論帳號是否被註冊過，都會顯示此資訊。避免被有心人士知道哪些電子信箱註冊過
		redirectAttributes.addFlashAttribute("successMsg", "密碼重設信件已發送至您的信箱，請於5分鐘內完成驗證!");
		return "redirect:/loginAndRegister/inputForgetPwd";
	}
	
	@PostMapping("verifyForgetPwd")
	public String verifyForgetPwd(@RequestParam("verifyCode") String userInputCode,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		String email = (String) session.getAttribute("forgetPwdEmail");
		if(email == null) {
			 model.addAttribute("errorMsg", "驗證失敗，請重新申請");
		     return "front-end/loginAndRegister/forgetPwd";
		}
		
		// 驗證
		if(passwordService.verify(email, userInputCode)) {
			
			// 驗證成功後，將驗證碼當新密碼存入資料庫
	        memberService.updatePassword(email, userInputCode);
	        session.removeAttribute("forgetPwdEmail");
			redirectAttributes.addFlashAttribute("successMsg", "密碼已更新，請使用新密碼重新登入!");
			
			return "redirect:/loginAndRegister/memberLogin";

		}
		
		model.addAttribute("errorMsg", "驗證失敗，請重新再試!");
		return "front-end/loginAndRegister/inputForgetPwd";
	}
}

