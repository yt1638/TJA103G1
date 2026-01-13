package com.showise.member.controller;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.eachmovietype.model.EachMovieTypeVO;
import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.memberclass.model.MemberClassService;
import com.showise.memberprefertype.model.MemberPreferTypeService;
import com.showise.memberprefertype.model.MemberPreferTypeVO;
import com.showise.movie.model.MovieService;
import com.showise.movie.model.MovieVO;
import com.showise.movietype.model.MovieTypeVO;
import com.showise.order.model.OrderService;
import com.showise.order.model.OrderVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@RequestMapping("/member")
@Controller
public class FrontMemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberClassService memberClassService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MemberPreferTypeService memberPreferTypeService;
	
	@Autowired
	private MovieService movieService;
	
	
	
	@GetMapping("mainMemberPage")
	public String mainMemberPage(Model model, HttpSession session) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		} 
		// 計算累積消費，並寫回資料庫
	    memberService.updateAccumulatedConsumption(loginMember.getMemberId());

	    // 重新抓最新會員資料（包含更新後的累積消費）
	    loginMember = memberService.getOneMember(loginMember.getMemberId());

	    // 判斷會員等級
	    loginMember = memberClassService.prepareMemberInfo(loginMember);
		
		model.addAttribute("loginMember", loginMember);	// 因為Thymeleaf預設是從Model取資料，而現在資料是在session中，因此將session中的資料存到model
		return "front-end/member/mainMemberPage";
	}
	
	
	@GetMapping("updateMemberData")
	public String updateMemberData(Model model, HttpSession session) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
		model.addAttribute("loginMember", loginMember);
		return "front-end/member/updateMemberData";
	}
	
	
	@GetMapping("myMemberClass")
	public String myMemberClass(Model model, HttpSession session) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
		// 計算累積消費，並寫回資料庫
	    memberService.updateAccumulatedConsumption(loginMember.getMemberId());

	    // 重新抓最新會員資料（包含更新後的累積消費）
	    loginMember = memberService.getOneMember(loginMember.getMemberId());

	    // 判斷會員等級
	    loginMember = memberClassService.prepareMemberInfo(loginMember);

	    
		model.addAttribute("loginMember", loginMember);
		return "front-end/member/myMemberClass";
	}
	
	
	@GetMapping("changePwd")
	public String changePwd(Model model, HttpSession session) {
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
		model.addAttribute("loginMember", loginMember);	// 因為Thymeleaf預設是從Model取資料，而現在資料是在session中，因此將session中的資料存到model
		return "front-end/member/changePwd";
	}
	
	
	@GetMapping("memberOrder")
	@Transactional(readOnly = true)		// 確保Hibernate Session 在整個方法期間保持開啟(但只讀，不寫)
	public String memberOrder(@RequestParam(defaultValue = "0") int page, 
			Model model, 
			HttpSession session) { 
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		if (loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
	    // 重新從資料庫抓取最新MemberVO(因為原本從session取得的loginMember物件，已經是detached狀態，Hibernate Session在當時已關閉
		// 而Lazy關聯需要在Session 活著的情況下才能初始化)
		// 因此這邊重新從資料庫抓取最新的MemberVO
	    MemberVO member = memberService.getOneMember(loginMember.getMemberId());

	    List<OrderVO> orders = member.getOrder();  // 因為在Session內，Lazy可以初始化
		
	    // 分頁 (一頁一筆)
	    int pageSize = 1;                
	    int totalOrders = orders.size();
	    int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

	    // 避免頁數超出，若小於第一頁，強制導回第一頁
	    if (page < 0) {
	    	page = 0;
	    }
	    // 超過總頁數，強制導回最後一頁
	    if (page >= totalPages && totalPages > 0) {
	    	page = totalPages - 1;
	    }

	    OrderVO order = null;
	    // 因為是一頁一筆訂單資料，因此page本身就等於List的index
	    if (!orders.isEmpty()) {
	        order = orders.get(page);    
	    }

	    
		// 強制初始化lazy關聯，避免可能發生LazyInitializationException
	    if (order != null) {
	        if (order.getSession() != null) {
	            order.getSession().getMovie();
	        }
	        if (order.getOrderTickets() != null) {
	            order.getOrderTickets().size();
	        }
	        if (order.getOrderFoods() != null) {
	            order.getOrderFoods().size();
	        }
	    }

		
	    model.addAttribute("order", order);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);

		return "front-end/member/memberOrder";
	}
	
	
	@GetMapping("memberNotify")
	@Transactional(readOnly = true)
	public String memberNotify(@RequestParam(defaultValue = "0") int page, 
			Model model, 
			HttpSession session) { 
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		if(loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
		MemberVO member = memberService.getOneMember(loginMember.getMemberId());
		List<OrderVO> orders = member.getOrder();
		                
	    int totalOrders = orders.size();
	    
	    // 將是否有資料傳回前台頁面
	    boolean hasData = (totalOrders > 0);
	    
	    
	    // 分頁
	    int pageSize = 1;
	    int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

	    
	    // 避免頁數超出，若小於第一頁，強制導回第一頁
	    if (page < 0) {
	    	page = 0;
	    }
	    // 超過總頁數，強制導回最後一頁
	    if (page >= totalPages && totalPages > 0) {
	    	page = totalPages - 1;
	    }

	    OrderVO order = null;
	    
	    // 因為是一頁一筆訂單資料，因此page本身就等於List的index
	    if (!orders.isEmpty()) {
	        order = orders.get(page);    
	    }

	    
		// 強制初始化lazy關聯，避免可能發生LazyInitializationException
	    if (order != null) {
	        if (order.getSession() != null) {
	            order.getSession().getMovie();
	        }
	        if (order.getOrderTickets() != null) {
	            order.getOrderTickets().size();
	        }
	        if (order.getOrderFoods() != null) {
	            order.getOrderFoods().size();
	        }
	    }

	    model.addAttribute("hasData", hasData);
	    model.addAttribute("order", order);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    
	    System.out.println("hasData=" + hasData + ", currentPage=" + page + ", totalPages=" + totalPages);

		return "front-end/member/memberNotify";
	}
	
	
	@GetMapping("memberTicket")
	@Transactional
	public String memberTicket(@RequestParam(defaultValue = "0") int page, 
			Model model, 
			HttpSession session) { 
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		if (loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		

	    MemberVO member = memberService.getOneMember(loginMember.getMemberId());

	    // 將越新的訂單，放在越前面的頁數
	    List<OrderVO> orders = member.getOrder();
	    orders.sort((o1, o2) -> o2.getOrderCreateTime().compareTo(o1.getOrderCreateTime()));
		
	    // 分頁 (一頁一筆)
	    int pageSize = 1;                
	    int totalOrders = orders.size();
	    int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

	    // 避免頁數超出，若小於第一頁，強制導回第一頁
	    if (page < 0) {
	    	page = 0;
	    }
	    // 超過總頁數，強制導回最後一頁
	    if (page >= totalPages && totalPages > 0) {
	    	page = totalPages - 1;
	    }

	    OrderVO order = null;
	    // 因為是一頁一筆訂單資料，因此page本身就等於List的index
	    if (!orders.isEmpty()) {
	        order = orders.get(page);    
	    }

	    
		// 強制初始化lazy關聯，避免可能發生LazyInitializationException
	    if (order != null) {
	        if (order.getSession() != null) {
	            order.getSession().getMovie();
	        }
	        if (order.getOrderTickets() != null) {
	            order.getOrderTickets().size();
	        }
	        if (order.getOrderFoods() != null) {
	            order.getOrderFoods().size();
	        }
	        
	        // 若還沒有QRCode，先生成一個具有唯一性的隨機碼，並儲存到資料庫。後續會使用這個隨機碼來生成QrCode
	        if(order.getQrCode() == null || order.getQrCode().isEmpty()) {
				String verifyCode = java.util.UUID.randomUUID().toString().replace("-", "");
				order.setQrCode(verifyCode);
				orderService.updateOrder(order);
			}

	    }
	    
	    model.addAttribute("order", order);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);

		return "front-end/member/memberTicket";
	}
	
	
	@GetMapping("recommend")
	public String recommend(HttpSession session, Model model) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		if(loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
		// 取得會員喜好電影類型Id
		List<MemberPreferTypeVO> preferList = memberPreferTypeService.getByMemberId(loginMember.getMemberId());
		List<Integer> preferTypeIds = new ArrayList<>();
		for(MemberPreferTypeVO prefer : preferList) {
			preferTypeIds.add(prefer.getMovieType().getMovieTypeId());
		}
		
		// 取得全部電影
		List<MovieVO> allMovies = movieService.listAll();
		Map<MovieVO, Integer> weightMap = new HashMap<>();
		
		// 計算 每部電影 和 會員喜好電影類型 的匹配次數
		for(MovieVO movie : allMovies) {
			
			// 取得該電影的類型關聯（中介表）
		    Set<EachMovieTypeVO> eachMovieTypes = movie.getEachMovieTypes();
		    int matchCount = 0;
		    
		    // 進行比對
		    for(EachMovieTypeVO emt : eachMovieTypes) {
		    	MovieTypeVO movieType = emt.getMovieType();
		    	Integer movieTypeId = movieType.getMovieTypeId();
		    	
		    	if (preferTypeIds.contains(movieTypeId)) {
		    		matchCount++;
		        }
		    }
		    
		    // 記錄此電影的匹配數
		    weightMap.put(movie, matchCount);
		}
		
		 // 再將weightMap按匹配次數分組(匹配次數相同的電影放在同一組)
		   // 匹配次數 相同匹配次數的電影列表
	    Map<Integer, List<MovieVO>> groupMap = new HashMap<>();
	    
	    for (Map.Entry<MovieVO, Integer> entry : weightMap.entrySet()) {
	        Integer weight = entry.getValue();
	        
	        // 檢查這個權重在groupMap中是否已存在
	        if (!groupMap.containsKey(weight)) {
	            groupMap.put(weight, new ArrayList<>());	// 沒有這個weight，新建一個空的ArrayList到Map中
	        }
	        groupMap.get(weight).add(entry.getKey());		// 取得此權重對應的ArrayList，再將電影加進去
	    }

	    // 權重由大到小排序
	    List<Integer> weights = new ArrayList<>(groupMap.keySet());
	    Collections.sort(weights, Collections.reverseOrder());

	    // 電影依權重排序，若相同權重則隨機
	    List<MovieVO> recommended = new ArrayList<>();
	    
	    for (Integer w : weights) {
	        List<MovieVO> movies = groupMap.get(w);
	        Collections.shuffle(movies); // 同權重隨機排序(避免每次推薦順序都一樣)
	        
	        for (MovieVO m : movies) {
	            recommended.add(m);
	            if (recommended.size() >= 6) break; // 最多推薦6部電影
	        }
	        
	        if (recommended.size() >= 6) break;
	    }

	    // 若會員沒有偏好，隨機推薦6部電影
	    if (preferTypeIds.isEmpty()) {
	        Collections.shuffle(allMovies);
	        recommended.clear();
	        for (int i = 0; i < 6 ; i++) {
	            recommended.add(allMovies.get(i));
	        }
	    }

	    
	    model.addAttribute("loginMember", loginMember);
	    model.addAttribute("recommendedMovies", recommended);
		
		return "front-end/member/recommend";
	}
	
	@GetMapping("/movie/image/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> getMovieImage(@PathVariable Integer id) {
	    MovieVO movie = movieService.getById(id);

	    if (movie == null || movie.getImage() == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok()
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)	// 告訴瀏覽器，這是一個「二進位檔案」。不論圖片是jpg、png、webp皆可
	            .body(movie.getImage());
	}

	
	@GetMapping("memberStyle")
	public String memberStyle(HttpSession session, Model model) {
		
		MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
		
		if (loginMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
	
		List<MemberPreferTypeVO> memberPreferTypeVOs = memberPreferTypeService.getByMemberId(loginMember.getMemberId());
		
		// 存放已選的電影類型
		List<Integer> selectTypeIds = new ArrayList<>();
		
		for(MemberPreferTypeVO  mpt : memberPreferTypeVOs) {
			MovieTypeVO movieType = mpt.getMovieType();
			if (movieType != null) {
	            selectTypeIds.add(movieType.getMovieTypeId());
	        }
		}
		
		model.addAttribute("selectTypeIds", selectTypeIds);
		return "front-end/member/memberStyle";
	}
	
	
	
	
	@PostMapping("edit")
	public String editMember(@Valid @ModelAttribute("loginMember") MemberVO formMember,
			BindingResult result, 
			HttpSession session,
			Model model) {
		
		if(result.hasErrors()) {
			return "front-end/member/updateMemberData";
		}
		
		// 從session取得完整會員資料
		MemberVO sessionMember = (MemberVO) session.getAttribute("loginMember");
		if (sessionMember == null) {
			return "redirect:/loginAndRegister/memberLogin";
		}
		
		// 只更新可以修改的欄位(name和phone，而email和birdate不可修改)
	    sessionMember.setName(formMember.getName());
	    sessionMember.setPhone(formMember.getPhone());
	    
		memberService.updateMember(sessionMember);				// 寫入資料庫
		session.setAttribute("loginMember", sessionMember);	// 更新session
		return "redirect:/member/mainMemberPage";
	}
	
	@PostMapping("changePassword")
	public String changePwd(
			@RequestParam("newPwd")String newPwd,
			@RequestParam("confirmPwd")String confirmPwd,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAtt) {
		
		if (!newPwd.matches("^[a-zA-Z0-9_]{6,20}$")) {
		    model.addAttribute("changePwdError", "密碼格式不正確，請輸入 6~20 位英文字母、數字或底線");
		    return "front-end/member/changePwd";
		}

		
		if(newPwd == null || (newPwd.trim().isEmpty()) ||
			confirmPwd == null || (confirmPwd.trim().isEmpty()) ) {
			model.addAttribute("changePwdError", "請確認兩個欄位是否都有輸入");
			return "front-end/member/changePwd";
		}
		
		// 檢查兩次密碼是否一致
		if(!newPwd.equals(confirmPwd)) {
			model.addAttribute("changePwdError", "兩次輸入的密碼不一致，請再次確認");
			return "front-end/member/changePwd";
		}
		
		// 取得目前登入會員
		MemberVO member = (MemberVO) session.getAttribute("loginMember");
		
		// 設定新密碼
		member.setPassword(newPwd);
		
		// 更新資料庫
		memberService.updateMember(member);
		
		// addFlashAttribute會在redirect 後顯示一次(暫存到session，下一次request取出，並清除)
		redirectAtt.addFlashAttribute("changePwdSuccess", "密碼修改成功!");
		
		return "redirect:/member/changePwd";
	}
	
	@PostMapping("updateMemberStyle")
	public String updateMemberStyle(
	        HttpSession session,
	        @RequestParam(value = "styleId", required = false) List<Integer> styleIds,
	        RedirectAttributes redirectAttributes) {

	    MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
	    if (loginMember == null) {
	        return "redirect:/loginAndRegister/memberLogin";
	    }
	    
	    memberPreferTypeService.saveMemberPreferTypes(loginMember, styleIds);

	    redirectAttributes.addFlashAttribute("successMsg", "已儲存您的喜好電影類型!");
	    return "redirect:/member/memberStyle";
	}

	@PostMapping("verify")
    @Transactional
    public String verifyTicket(@RequestParam("orderId") Integer orderId,
                               @RequestParam(defaultValue = "0") int page,
                               Model model,
                               HttpSession session) {

        MemberVO loginMember = (MemberVO) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/loginAndRegister/memberLogin";
        }

        OrderVO order = orderService.getDetails(orderId);
        if (order != null && !Boolean.TRUE.equals(order.getUsed())) {
            order.setUsed(true);
            orderService.updateOrder(order);
            model.addAttribute("verifyMessage", "驗票成功！");
        } else {
            model.addAttribute("verifyMessage", "此票券已驗票或無效！");
        }

        // 重新載入頁面資料
        return memberTicket(page, model, session);
    }
}

