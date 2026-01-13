package com.showise.member.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.memberclass.model.MemberClassService;
import com.showise.memberclass.model.MemberClassVO;
import com.showise.memberprefertype.model.MemberPreferTypeService;
import com.showise.memberprefertype.model.MemberPreferTypeVO;
import com.showise.movietype.model.MovieTypeVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/bmember")
public class MemberController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	MemberClassService memberClassService;
	
	@Autowired
	MemberPreferTypeService memberPreferTypeService;
	
	
	@GetMapping("select_page")
	public String selectPage(ModelMap model) {
	    List<MemberVO> list = memberService.getAll();
	    model.addAttribute("memberListData", list);
	    model.addAttribute("pageTitle","會員管理");
		model.addAttribute("content","back-end/bmember/select_page :: content");
		return "back-end/layout/admin-layout";
	}

	
	@GetMapping("addMember")
	public String addMember(ModelMap model) {
		MemberVO member = new MemberVO();
		MemberPreferTypeVO memberPreferType = new MemberPreferTypeVO();
		model.addAttribute("memberVO", member);
		model.addAttribute("memberPreferTypeVO", memberPreferType);
		model.addAttribute("pageTitle","新增會員資料");
		model.addAttribute("content","back-end/bmember/addMember :: content");
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("listAllMember")
	public String listAllMember(ModelMap model) {
		List<MemberVO> list = memberService.getAll();
		
		// 更新所有會員的累積消費與會員等級
	    for(MemberVO member : list) {
	        memberService.updateAccumulatedConsumption(member.getMemberId());
	        memberClassService.prepareMemberInfo(member);
	    }
		
		model.addAttribute("memberListData", list);
	    model.addAttribute("pageTitle","所有會員資料");
		model.addAttribute("content","back-end/bmember/listAllMember :: content");
		return "back-end/layout/admin-layout";
	}

	
	@PostMapping("insert")
	public String insert(@Valid @ModelAttribute("memberVO") MemberVO member, 
			BindingResult result,
			Model model,
			@RequestParam(value= "styleId", required = false) List<Integer> styleIds,
			RedirectAttributes redirectAttributes) {
		
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		if(result.hasErrors()) {
			model.addAttribute("selectedStyleIds", styleIds);
			model.addAttribute("pageTitle","新增會員資料");
			model.addAttribute("content","back-end/bmember/addMember :: content");
			return "back-end/layout/admin-layout";
		}
		
		// 檢驗email是否被註冊/新增過
		if (memberService.existsByEmail(member.getEmail())) {
	        result.rejectValue("email", "member.email.exists", "此電子信箱已被註冊，請使用其他信箱");
	        // rejectValue會直接綁定到th:errors，不用自己加errorMessage。
	        // email: 欄位名稱(field) | member.email.exists: 錯誤代碼(error code) |最後是 defaultMessage
	               	

	        model.addAttribute("selectedStyleIds", styleIds);
	        return "back-end/bmember/addMember";
	    }
		
		//******************2.開始新增資料********************************
		memberService.addMember(member);
		memberPreferTypeService.saveMemberPreferTypes(member, styleIds);
		
		//******************3.新增完成，準備轉交****************************	
		
		redirectAttributes.addAttribute("success", "-(新增成功)");
		return "redirect:/bmember/listAllMember";			
	}
	
	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("memberId") String memberId, ModelMap model) {
		
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始查詢資料********************************
		
		// 累積消費金額
		Integer id = Integer.valueOf(memberId);

	    // 計算累積消費，並寫回資料庫
	    memberService.updateAccumulatedConsumption(id);

	    // 取得最新會員資料
	    MemberVO member = memberService.getOneMember(Integer.valueOf(memberId));

	    // 判斷會員等級
	    member = memberClassService.prepareMemberInfo(member);
		
		
		List<MemberPreferTypeVO> memberPreferTypeVOs = memberPreferTypeService.getByMemberId(member.getMemberId());
		
		// 存放已選的電影類型
		List<Integer> selectTypeIds = new ArrayList<>();
		
		for(MemberPreferTypeVO  mpt : memberPreferTypeVOs) {
			MovieTypeVO movieType = mpt.getMovieType();
			if (movieType != null) {
	            selectTypeIds.add(movieType.getMovieTypeId());
	        }
		}
		
		//******************3.查詢完成，準備轉交****************************	
		
		model.addAttribute("memberVO", member);
		model.addAttribute("selectTypeIds", selectTypeIds);
		model.addAttribute("pageTitle","修改會員資料");
		model.addAttribute("content","back-end/bmember/update_member_insert :: content");
		return "back-end/layout/admin-layout";
	}
	
	
	@PostMapping("update")
	public String update(@Valid MemberVO member, 
			BindingResult result, 
			ModelMap model,
			@RequestParam(value = "styleId", required = false) List<Integer> styleIds) {
		
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		MemberVO existing = memberService.getOneMember(member.getMemberId());
	    member.setBirthdate(existing.getBirthdate());		// 因為預設不可修改生日，因此保留原本的birthdate，不驗證null
	    if (member.getMemberClass() == null) {				// 確保 memberClass 不為 null
	        member.setMemberClass(new MemberClassVO());
	    }
	    
		if(result.hasErrors()) {
			model.addAttribute("memberVO", member);
			model.addAttribute("memberClassListData", memberClassService.getAll());
			model.addAttribute("selectTypeIds", styleIds);
			model.addAttribute("pageTitle","更新會員資料");
			model.addAttribute("content","back-end/bmember/update_member_insert :: content");
			return "back-end/layout/admin-layout";
		}
		
		//******************2.開始修改資料********************************	
		
		 // 根據累積消費金額，自動判定會員等級 
	    Integer acc = member.getAccConsumption() == null ? 0 : member.getAccConsumption();
	    if(acc >= 10000) {
	        member.getMemberClass().setMemberClassId(3); // 白金會員
	    } else if(acc >= 5000) {
	        member.getMemberClass().setMemberClassId(2); // 黃金會員
	    } else {
	        member.getMemberClass().setMemberClassId(1); // 一般會員
	    }
		
		memberService.updateMember(member);
		memberPreferTypeService.saveMemberPreferTypes(member, styleIds);
		
		//******************3.修改完成，準備轉交****************************
		
		member = memberService.getOneMember(Integer.valueOf(member.getMemberId()));
		model.addAttribute("success", "-(修改成功)");
		model.addAttribute("memberVO", member);
		model.addAttribute("pageTitle","會員資料");
		model.addAttribute("content","back-end/bmember/listOneMember :: content");
		return "back-end/layout/admin-layout";
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
		return "redirect:/bmember/listAllMember";	//這邊用redirect，避免重複送出
	}
	
	
	@ModelAttribute("memberClassListData")
	protected List<MemberClassVO>  referenceListData(){
		List<MemberClassVO> list = memberClassService.getAll();
		return list;
	}
	
}
