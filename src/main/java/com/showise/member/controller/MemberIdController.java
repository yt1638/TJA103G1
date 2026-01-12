package com.showise.member.controller;

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

import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
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
@RequestMapping("/bmember")
public class MemberIdController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	MemberClassService memberClassService;
	
	@PostMapping("/searchByName")
	public String searchByName(Model model,
			@NotEmpty(message = "會員名稱: 請勿空白") @RequestParam("keyword") String keyword ) {
	    
		// 搜尋會員名稱包含keyword的會員
	    List<MemberVO> resultList = memberService.findByNameContaining(keyword);

	    // 傳送會員清單給下拉選單
	    List<MemberVO> allMembers = memberService.getAll();
	    List<MemberClassVO> memberClassList = memberClassService.getAll();
	    
	    model.addAttribute("memberListData", allMembers);
	    model.addAttribute("memberClassVO", new MemberClassVO());
	    model.addAttribute("memberClassListData", memberClassList);
	    
	    if (resultList.isEmpty()) {
	        model.addAttribute("errorMessage", "查無資料");
	        return "back-end/bmember/select_page";
	    }
	    
	    model.addAttribute("searchResult", resultList);

	    return "back-end/bmember/listSearchResult"; 
	}

	
	
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
			
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		@NotEmpty(message = "會員編號: 請勿空白")
		@Digits(integer = 4, fraction = 0, message = "會員編號: 請填數字-請勿超過{integer}位數")
		@Min(value = 1, message = "會員編號: 不可小於{value}")
		@RequestParam("memberId") String memberId,	// 使用String: 避免使用者輸入非數字時，直接轉型發生錯誤。先選擇String，驗證完才轉成Integer
		ModelMap model) {

			//******************2.開始查詢資料********************************
			MemberVO member = memberService.getOneMember(Integer.valueOf(memberId)); 
			
			// 不論查詢成功或失敗，都會回到select_page.html，而這個頁面都需要會員清單、會員等級下拉選單、空的memberClassVO來綁定表單
			List<MemberVO> list = memberService.getAll();
			model.addAttribute("memberListData", list);
			model.addAttribute("memberClassVO", new MemberClassVO());
			List<MemberClassVO> list2 = memberClassService.getAll();
			model.addAttribute("memberClassListData", list2);
			
			if(member == null) {
				model.addAttribute("errorMessage", "查無資料");
				return "back-end/bmember/select_page";
			}
			
			//******************3.查詢完成，準備轉交****************************
			model.addAttribute("memberVO", member);
			return "back-end/bmember/listOneMember";		// 查詢完成後，轉交listOneMember.html
	}
		
	// 當使用者輸入的資料未通過驗證
	// ExceptionHandler 進行錯誤處理(當這個Controller內發生ConstraintViolationException時，由這個方法負責處理，而不是讓Spring顯示預設的錯誤頁面)
	@ExceptionHandler(value = {ConstraintViolationException.class})
	public ModelAndView handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<?> violation : violations) {
			strBuilder.append(violation.getMessage() + "<br>");
		}
		
		
		List<MemberVO> list = memberService.getAll();
		model.addAttribute("memberListData", list);
		model.addAttribute("memberClassVO", new MemberClassVO());
		List<MemberClassVO> list2 = memberClassService.getAll();
		model.addAttribute("memberClassListData", list2);
		String message = strBuilder.toString();
		return new ModelAndView("back-end/bmember/select_page", "errorMessage", "請修正以下錯誤:<br>" + message);
	}
	
		
}
