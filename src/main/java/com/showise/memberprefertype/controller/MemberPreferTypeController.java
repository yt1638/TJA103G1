package com.showise.memberprefertype.controller;

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
import com.showise.memberprefertype.model.MemberPreferTypeService;
import com.showise.memberprefertype.model.MemberPreferTypeVO;
import com.showise.movietype.model.MovieTypeService;
import com.showise.movietype.model.MovieTypeVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/memberPreferType")
public class MemberPreferTypeController {

	@Autowired
	MemberPreferTypeService memberPreferTypeService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	MovieTypeService movieTypeService;
	
	@GetMapping("select_page")
	public String select_page(ModelMap model) {
		List<MemberPreferTypeVO> list = memberPreferTypeService.getAll();
	    model.addAttribute("memberPreferTypeListData", list);
		return "back-end/memberPreferType/select_page";
	}
	
	@GetMapping("addMemberPreferType")
	public String addMemberPreferType(ModelMap model) {
		MemberPreferTypeVO memberPreferTypeVO = new MemberPreferTypeVO();
		memberPreferTypeVO.setMember(new MemberVO());		// 避免是空值
		memberPreferTypeVO.setMovieType(new MovieTypeVO());	// 避免是空值
		
		model.addAttribute("memberPreferTypeVO", memberPreferTypeVO);
		return "back-end/memberPreferType/addMemberPreferType";
	}
	
	@GetMapping("listAllMemberPreferType")
	public String listAllMemberPreferType(ModelMap model) {
		List<MemberPreferTypeVO> list = memberPreferTypeService.getAll();
	    model.addAttribute("memberPreferTypeListData", list);
		return "back-end/memberPreferType/listAllMemberPreferType";
	}
	
	@PostMapping("insert")
	public String insert(@Valid MemberPreferTypeVO memberPreferTypeVO, BindingResult result, ModelMap model) {
		
		// 防止因為null而產生錯誤
	    if (memberPreferTypeVO.getMember() == null) {
	        memberPreferTypeVO.setMember(new MemberVO());
	    }

	    if (memberPreferTypeVO.getMovieType() == null) {
	        memberPreferTypeVO.setMovieType(new MovieTypeVO());
	    }
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		if(result.hasErrors()) {
			return "back-end/memberPreferType/addMemberPreferType";
		}
		
		//******************2.開始新增資料********************************	
		memberPreferTypeService.addMemberPreferType(memberPreferTypeVO);
		
		//******************3.新增完成，準備轉交****************************	
		List<MemberPreferTypeVO> list = memberPreferTypeService.getAll();
		model.addAttribute("memberPreferTypeListData" ,list);
		model.addAttribute("success", "-(新增成功)");
		return "redirect:/memberPreferType/listAllMemberPreferType";
	}
	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("memberPreferTypeId") String memberPreferTypeId, ModelMap model) {
		
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始查詢資料********************************	
		MemberPreferTypeVO memberPreferTypeVO = memberPreferTypeService.getOneMemberPreferType(Integer.valueOf(memberPreferTypeId));
		
		//******************3.查詢完成，準備轉交****************************	
		model.addAttribute("memberPreferTypeVO", memberPreferTypeVO);
		return "back-end/memberPreferType/update_member_prefer_type_insert";
	}
	
	@PostMapping("update")
	public String update(@Valid MemberPreferTypeVO memberPreferTypeVO, BindingResult result, ModelMap model) {
		
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		if(result.hasErrors()) {
			return "back-end/memberPreferType/listOneMemberPreferType";
		}
		
		//******************2.開始修改資料********************************	
		memberPreferTypeService.updateMemberPreferType(memberPreferTypeVO);
		
		//******************3.修改完成，準備轉交****************************	
		model.addAttribute("success", "-(修改完成)");
		memberPreferTypeVO = memberPreferTypeService.getOneMemberPreferType(Integer.valueOf(memberPreferTypeVO.getMemberPreferTypeId()));
		model.addAttribute("memberPreferTypeVO", memberPreferTypeVO);
		return "back-end/memberPreferType/listAllMemberPreferType";
	}
	
	@PostMapping("delete")
	public String delete(@RequestParam("memberPreferTypeId") String memberPreferTypeId, ModelMap model) {
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		//******************2.開始刪除資料********************************	
		memberPreferTypeService.deleteMemberPreferType(Integer.valueOf(memberPreferTypeId));
		
		//******************3.刪除完成，準備轉交****************************	
		List<MemberPreferTypeVO> list = memberPreferTypeService.getAll();
		model.addAttribute("memberPreferTypeListData", list);
		model.addAttribute("success", "-(刪除成功)");
		return "redirect:/memberPreferType/listAllMemberPreferType";
	}
	
	// 會員
	@ModelAttribute("memberListData")
	protected List<MemberVO> referenceMemberListData(){
		List<MemberVO> list = memberService.getAll();
		return list;
	}
	
	// 電影類型
	@ModelAttribute("movieTypeListData")
	protected List<MovieTypeVO> referenceMovieTypeListData(){
		List<MovieTypeVO> list = movieTypeService.listAll();
		return list;
	}
	
	// 會員喜好電影類型
	@ModelAttribute("memberPreferTypeListData")
	protected List<MemberPreferTypeVO> referenceMemberPreferTypeListData(){
		List<MemberPreferTypeVO> list = memberPreferTypeService.getAll();
		return list;
	}
}
