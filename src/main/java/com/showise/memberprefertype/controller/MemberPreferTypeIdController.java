package com.showise.memberprefertype.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.memberprefertype.model.MemberPreferTypeService;
import com.showise.memberprefertype.model.MemberPreferTypeVO;
import com.showise.movietype.model.MovieTypeService;
import com.showise.movietype.model.MovieTypeVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/memberPreferType")
public class MemberPreferTypeIdController {

	@Autowired
	MemberPreferTypeService memberPreferTypeService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	MovieTypeService movieTypeService;
	
//	三種方式進行查詢，回到select_page所需的共用資料
	private void prepareForSelectPage(ModelMap model) {
		
//		會員喜好
		model.addAttribute("memberPreferTypeVO", new MemberPreferTypeVO());
		model.addAttribute("memberPreferTypeListData", memberPreferTypeService.getAll());
		
//		會員
		model.addAttribute("memberVO", new MemberVO());
		model.addAttribute("memberListData", memberService.getAll());
		
//		電影風格
		model.addAttribute("movieTypeVO", new MovieTypeVO());
		model.addAttribute("movieTypeListData", movieTypeService.listAll());
	}
	
	
//	顯示單筆會員喜好資料
//	@GetMapping("select_one")
//	public String listOneMemberPreferType(@RequestParam Integer id, Model model) {
//		
//	    MemberPreferTypeVO memberPreferTypeVO = memberPreferTypeService.getOneMemberPreferType(id);
//	    if(memberPreferTypeVO == null){
//	        model.addAttribute("error", "找不到該筆資料");
//	        return "back-end/memberPreferType/select_page";
//	    }
//
//	    model.addAttribute("memberPreferTypeVO", memberPreferTypeVO);
//	    return "back-end/memberPreferType/listOneMemberPreferType";
//	}
	
	
//	依照會員喜好類型編號進行查詢
	@PostMapping("getOneByPreferTypeId")
	public String getOneByPreferTypeId(
			//******************1.接收請求參數，輸入格式的錯誤處理	******************
			@NotEmpty(message = "會員喜好電影風格的編號: 請勿空白")
			@Digits(integer = 6, fraction = 0, message = "會員喜好電影風格的編號: 請填數字-請勿超過{integer}位數")
			@Min(value = 1, message = "會員喜好電影風格的編號: 不可小於{value}")
			@RequestParam("memberPreferTypeId") String memberPreferTypeId,
			ModelMap model) {
		
		//******************2.開始查詢資料********************************	
		prepareForSelectPage(model);
		try {
			Integer id = Integer.valueOf(memberPreferTypeId.trim());
			MemberPreferTypeVO memberPreferTypeVO = memberPreferTypeService.getOneMemberPreferType(id);
			
			if(memberPreferTypeVO == null) {
				prepareForSelectPage(model);
				model.addAttribute("errorMessage", "查無資料");
				return "back-end/memberPreferType/select_page";
			}
			
			//******************3.查詢完成，準備轉交****************************	
			model.addAttribute("memberPreferTypeVO", memberPreferTypeVO);
			return "back-end/memberPreferType/listOneMemberPreferType";		// 查詢完成後，轉交listOneMemberPreferType.html
			
			
		} catch(NumberFormatException e) {
            model.addAttribute("errorMessage", "會員喜好電影風格編號必須為數字");
            return "back-end/memberPreferType/select_page";
		}
		
	}
	
	
//	依照會員編號進行查詢
	@PostMapping("getOneByMemberId")
	public String getOneByMemberId(
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		@NotEmpty(message = "會員編號: 請勿空白")
		@Digits(integer = 4, fraction = 0, message = "會員編號: 請填數字-請勿超過{integer}位數")
		@Min(value = 1, message = "會員編號: 不可小於{value}")
		@RequestParam("memberId") String memberId,	// 使用String: 避免使用者輸入非數字時，直接轉型發生錯誤。先選擇String，驗證完才轉成Integer
		ModelMap model) {
		
		//******************2.開始查詢資料********************************	
		prepareForSelectPage(model);
		try {
			Integer id = Integer.valueOf(memberId.trim());
			List<MemberPreferTypeVO> list = memberPreferTypeService.getByMemberId(Integer.valueOf(memberId));
			
			if(list == null || list.isEmpty()) {
				prepareForSelectPage(model);
				model.addAttribute("errorMessage", "查無資料");
				return "back-end/memberPreferType/select_page";
			}
			
			//******************3.查詢完成，準備轉交****************************	
			model.addAttribute("memberPreferTypeVOList", list);
			return "back-end/memberPreferType/listOneMemberPreferType";		// 查詢完成後，轉交listOneMemberPreferType.html
		} catch(NumberFormatException e) {
			model.addAttribute("errorMessage", "會員編號須為數字");
		    return "back-end/memberPreferType/select_page";
		}
		
	}
	
	
//	依照電影類型編號進行查詢
	@PostMapping("getOneByMovieTypeId")
	public String getOneByMovieTypeId(
		//******************1.接收請求參數，輸入格式的錯誤處理	******************
		@NotEmpty(message = "電影類型編號: 請勿空白")
		@Digits(integer = 3, fraction = 0, message = "電影類型編號: 請填數字-請勿超過{integer}位數")
		@Min(value = 1, message = "電影類型編號: 不可小於{value}")
		@RequestParam("movieTypeId") String movieTypeId,
		ModelMap model) {
		
		//******************2.開始查詢資料********************************	
		prepareForSelectPage(model);
		
		try {
            Integer id = Integer.valueOf(movieTypeId.trim());
            List<MemberPreferTypeVO> list = memberPreferTypeService.getByMovieTypeId(id);
            
            if (list == null || list.isEmpty()) {
                model.addAttribute("errorMessage", "查無資料");
                return "back-end/memberPreferType/select_page";
            }
            
          //******************3.查詢完成，準備轉交****************************	
            model.addAttribute("memberPreferTypeVOList", list);
            return "back-end/memberPreferType/listOneMemberPreferType";		// 查詢完成後，轉交listOneMemberPreferType.html

        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "電影類型編號必須為數字");
            return "back-end/memberPreferType/select_page";
        }
		
	}
	
	
	// 當使用者輸入的資料未通過驗證
		// ExceptionHandler進行錯誤處理(當這個Controller內發生ConstraintViolationException時，由這個方法負責處理，而不是讓Spring顯示預設錯誤頁面)
	@ExceptionHandler(value = {ConstraintViolationException.class})
	public ModelAndView handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		
		StringBuilder strBuilder = new StringBuilder();
		for(ConstraintViolation<?> violation : violations) {
			strBuilder.append(violation.getMessage() + "<br>");
		}
		
		
		List<MemberPreferTypeVO> list = memberPreferTypeService.getAll();
		model.addAttribute("memberPreferTypeListData", list);
		
		model.addAttribute("memberVO", new MemberVO());
		List<MemberVO> memberList = memberService.getAll();
		model.addAttribute("memberListData", memberList);
		
		model.addAttribute("movieTypeVO", new MovieTypeVO());
		List<MovieTypeVO> movieTypeList = movieTypeService.listAll();
		model.addAttribute("movieTypeListData", movieTypeList);
		
		String message = strBuilder.toString();
		return new ModelAndView("back-end/memberPreferType/select_page", "errorMessage", "請修正以下錯誤:<br>" + message);
	}
				
}

