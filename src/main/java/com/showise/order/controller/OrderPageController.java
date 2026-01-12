package com.showise.order.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.food.model.FoodService;
import com.showise.food.model.FoodVO;
import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.movie.model.MovieService;
import com.showise.movie.model.MovieVO;
import com.showise.order.model.OrderDraft;
import com.showise.order.model.OrderService;
import com.showise.order.model.OrderVO;
import com.showise.order.model.SeatLockService;
import com.showise.seat.model.SeatRepository;
import com.showise.seat.model.SeatService;
import com.showise.seat.model.SeatVO;
import com.showise.session.model.SessionRepository;
import com.showise.session.model.SessionService;
import com.showise.session.model.SessionVO;
import com.showise.tickettype.model.TicketTypeService;
import com.showise.tickettype.model.TicketTypeVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//這隻是前台order

@Controller
@RequestMapping("/order")
@SessionAttributes("orderDraft") //spring會自動同步存到session scope保存，Spring MVC額外管理一個「流程型Session物件」
public class OrderPageController {
	@Autowired
	MovieService movieSvc;
	@Autowired
	TicketTypeService ticketTypeSvc;
	@Autowired
	FoodService foodSvc;
	@Autowired
	SessionService sessionSvc;
	@Autowired
	SessionRepository sessionRepo;
	@Autowired
	SeatService seatSvc;
	@Autowired
	SeatLockService seatLockSvc;
	@Autowired
	OrderService orderSvc;
	@Autowired
	MemberService memberSvc;
	@Autowired
	SeatRepository seatRepo;

  private static final long TTL_MS = 15 * 60 * 1000;//草稿效期:15分鐘的限制


  //第一次進入，建立訂單草稿
  //只在session沒有orderDraft時會被呼叫
  /**要記得檢查：登出要session.invalidate()！！！！！**/
  @ModelAttribute("orderDraft")
  public OrderDraft initDraft(HttpSession session) {
    OrderDraft od = new OrderDraft();
    
    MemberVO memberVO = (MemberVO) session.getAttribute("loginMember");
    Integer loginMemberId = (memberVO != null) ? memberVO.getMemberId() : null;
    
    od.setMemberId(loginMemberId);
//    od.setMemberId(3);
    od.setExpireAt(System.currentTimeMillis() + TTL_MS);  //失效時間：現在＋15分鐘
    od.setLockToken(UUID.randomUUID().toString());//用UUID生成一個token，用來綁seatlock
    return od;
  }

  //如果expireAt沒有值或已經<= now，就當作過期
  private boolean isExpired(OrderDraft od) {
    return od.getExpireAt() == null || od.getExpireAt() <= System.currentTimeMillis();
  }
  
  //用來重設15分鐘
  private void resetExpire(OrderDraft d) {
    d.setExpireAt(System.currentTimeMillis() + TTL_MS);
  }

  //1. GET/order：只讀draft
  @GetMapping
  public String orderPage(Model model,@ModelAttribute("orderDraft") OrderDraft draft,HttpSession session,SessionStatus sessionStatus) {
	  MemberVO memberVO = (MemberVO) session.getAttribute("loginMember");
	  Integer loginMemberId = (memberVO != null) ? memberVO.getMemberId() : null;

	  //若已登入但 draft.memberId 還沒塞，就補回來
	  if (loginMemberId != null && draft.getMemberId() == null) {
	      draft.setMemberId(loginMemberId);
	  }

	  //若 draft 綁的是別人（你原本的防切帳號邏輯）
	  if (loginMemberId != null && draft.getMemberId() != null && !loginMemberId.equals(draft.getMemberId())) {
	      sessionStatus.setComplete();
	      return "redirect:/order";
	  }

	  
	  if(loginMemberId != null && draft.getMemberId() != null && !loginMemberId.equals(draft.getMemberId())) {
		  sessionStatus.setComplete();
		  return "redirect:/order";
	  }
	  System.out.println("draft.memberId=" + draft.getMemberId());

	  //檢查草稿是否過期：一過期就清掉，redirect 回/order 重新建立草稿
      if (isExpired(draft)) {
    	  sessionStatus.setComplete();
    	  return "redirect:/order"; //重新發送 Get/order請求
      }
      
      /* ========= 1.從session回填（draft是空時）========= */
      boolean draftEmpty=draft.getMovieId() == null && draft.getDate() == null && draft.getSessionId() == null;
      if (draftEmpty) {
          Integer sMovieId=(Integer) session.getAttribute("movieId");
          LocalDate sDate=(LocalDate) session.getAttribute("date");
          Integer sSessionId=(Integer) session.getAttribute("sessionId");
          
//          Integer sMovieId=6;
//          LocalDate sDate=LocalDate.parse("2026-01-14");
//          Integer sSessionId=16;

          if (sMovieId != null || sDate != null || sSessionId != null) {
              draft.setMovieId(sMovieId);
              draft.setDate(sDate);
              draft.setSessionId(sSessionId);
              
              session.removeAttribute("movieId");
              session.removeAttribute("date");
              session.removeAttribute("sessionId");
          }
      }
      
      /* ========= 2.補齊顯示用欄位（movieName/sessionTimeText） ========= */
      if (draft.getMovieId() != null && draft.getMovieName() == null) {
          MovieVO movie = movieSvc.getById(draft.getMovieId());
          if (movie != null) {
              draft.setMovieName(movie.getNameTw());
          }
      }

      if (draft.getSessionId() != null && draft.getSessionTimeText() == null) {
          SessionVO sessionVO = sessionRepo.findById(draft.getSessionId()).orElse(null);
          if (sessionVO != null && sessionVO.getStartTime() != null) {
              String hhmm = sessionVO.getStartTime()
                      .toLocalDateTime()
                      .format(DateTimeFormatter.ofPattern("HH:mm"));
              draft.setSessionTimeText(hhmm);
          }
      }
      
      /* ========= 3.基本下拉選單、票券、餐飲的資料 ========= */
      model.addAttribute("movies",sessionSvc.listBookableMoviesNext7Days());//未來7天有場次可訂的電影
      model.addAttribute("ticketTypes", ticketTypeSvc.getAll()); //列出票種
      model.addAttribute("ticketFinalPriceMap",orderSvc.getFinalPricesByMember(draft.getMemberId()));//登入會員後的票種價格
      model.addAttribute("foods", foodSvc.listByStatus(1));//只列出上架的餐飲
      
      /* ========= 4.日期/場次 ========= */
      //使用者在後續選擇後，redirect回到這頁
      Integer movieId = draft.getMovieId();
      LocalDate date = draft.getDate();

      //dates：未來7天該電影有場次的日期，沒有選dates就是空的
      List<LocalDate> dates = (movieId != null)? sessionSvc.listDatesByMovieNext7Days(movieId): List.of();
      
      //sessions:該電影在該日期的場次，必須movieId + date都有才能查場次，缺一的話sessions就是空的
      List<SessionVO> sessions = (movieId != null && date != null)? sessionSvc.listSessionsByMovieAndDate(movieId, date): List.of();

      model.addAttribute("dates", dates);
      model.addAttribute("sessions", sessions);
      
      /* ========= 5.回填目前draft的選擇========= */
      //給前台畫面回填狀態用
      model.addAttribute("movieId", draft.getMovieId());
      model.addAttribute("date", draft.getDate());
      model.addAttribute("sessionId", draft.getSessionId());
      //給頁面刷新/回跳時的預設值回填用
      model.addAttribute("draft", draft);
      return "front-end/order/order";
  }


  // 2.POST/order/select-movie-date：使用者在選電影、或選日期
  //@DateTimeFormat:把"2026-01-04"字串轉成LocalDate
  @PostMapping("/select-movie-date")
  public String selectMovieDate(@RequestParam(required=false) Integer movieId,
                                @RequestParam(required=false)@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date,
                                @ModelAttribute("orderDraft") OrderDraft draft) {

    resetExpire(draft);//reset草稿15分鐘
    
    //如果換電影，就清掉所有該moviedId有關的資料
    if (movieId != null && !movieId.equals(draft.getMovieId())) {
      draft.setMovieId(movieId);
      
      //換電影：清掉下面其他的選擇
      draft.setDate(null);
      draft.setSessionId(null);
      draft.setSessionTimeText(null);
      draft.setTickets(List.of());
      draft.setFoods(List.of());
      draft.setTotalTicketQty(null);
      draft.setTicketTotal(null);
      draft.setFoodTotal(null);
      draft.setTotal(null);
      draft.setSelectedSeats(List.of());
    }

    //如果換日期，就清場次/座位
    if (date != null && !date.equals(draft.getDate())) {
      draft.setDate(date);

      //換日期：清掉場次/座位
      draft.setSessionId(null);
      draft.setSessionTimeText(null);
      draft.setSelectedSeats(List.of());
    }
    //補電影名稱
    if (movieId != null) {
    	  MovieVO movie = movieSvc.getById(movieId);
    	  draft.setMovieName(movie != null ? movie.getNameTw() : null);
    	} else {
    	  draft.setMovieName(null);
    	}
    
    return "redirect:/order";
  }

  //3.POST /order/select-session 選場次：使用者按某個場次時間
  @PostMapping("/select-session")
  public String selectSession(@RequestParam Integer sessionId,
                              @ModelAttribute("orderDraft") OrderDraft draft) {
	  //1.resetExpire
	  resetExpire(draft);
	  //2.設定draft.sessionId
	  draft.setSessionId(sessionId);
	  SessionVO session = sessionRepo.findById(sessionId).orElse(null);
	  
	  //3.把startTime轉成HH:mm，存進draft.sessionTimeText，讓畫面直接顯示
	  if (session != null) { 
    	Timestamp ts=session.getStartTime();
    	String hhmm=ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    	draft.setSessionTimeText(hhmm);
    	}
	  //4.換場次：清掉座位
	  draft.setSelectedSeats(List.of());
	  
	  //5.redirect 回到/order 更新畫面
	  return "redirect:/order";
  }

  //4.POST /order/prepare-seat：使用者選票、餐飲，按「選座位」
  //驗證草稿狀態是否完整、未過期
  //把表單送來的票種/餐飲qty解析成結構化清單 + 計算金額（寫進 draft）
  //確認至少1張票，然後redirect到 /order/seat 開始選位倒數
  @PostMapping("/prepare-seat")
  public String prepareSeat(@RequestParam Map<String,String> params,
                            @ModelAttribute("orderDraft") OrderDraft draft,
                            RedirectAttributes ra,SessionStatus sessionStatus) {

	if (isExpired(draft)) {
		 sessionStatus.setComplete();
		 return "redirect:/order"; //重新發送 Get/order請求
	 }
    if (draft.getMovieId() == null || draft.getDate() == null || draft.getSessionId() == null) {
      return "redirect:/order";
    }
    

    //從params解析tickets部分:ticket_{id}
    List<OrderDraft.TicketItem> tickets = new ArrayList<>();
    
    int totalQty = 0;
    BigDecimal ticketTotal=BigDecimal.ZERO;
    
    //後端重算金額
    Map<Integer, BigDecimal> finalPriceMap =orderSvc.getFinalPricesByMember(draft.getMemberId());
    
    //從params，把ticket_開頭的抓出來
    for (Map.Entry<String,String> paramsMap : params.entrySet()) {
      String ticket = paramsMap.getKey();
      if (!ticket.startsWith("ticket_")) continue;
      
      //從key拆出ticketTypeId，ex:ticket_1 → substring後是 "1" → 轉成Integer
      Integer ticketTypeId = Integer.valueOf(ticket.substring(7));
      int qty = Integer.parseInt(paramsMap.getValue());
      if (qty <= 0) continue;//不選的票種不寫入清單
      

      TicketTypeVO tt = ticketTypeSvc.getOneById(ticketTypeId);
      BigDecimal price =finalPriceMap.get(ticketTypeId);  	  
      BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));

      //組TicketItem寫入tickets 
      OrderDraft.TicketItem item = new OrderDraft.TicketItem();
      item.setTicketTypeId(ticketTypeId);
      item.setName(tt.getTicketName());
      item.setPrice(price);
      item.setQty(qty);
      item.setSubtotal(subtotal);

      tickets.add(item);
      totalQty += qty;
      ticketTotal =ticketTotal.add(subtotal);
    }
    
    if (totalQty <= 0) {
        ra.addFlashAttribute("errorMessage", "請至少選 1 張票");
        return "redirect:/order";
    }

    // foods部分:food_{id}
    List<OrderDraft.FoodItem> foods = new ArrayList<>();
    
    BigDecimal foodTotal = BigDecimal.ZERO;

    for (Map.Entry<String,String> paramsMap : params.entrySet()) {
      String food = paramsMap.getKey();
      if (!food.startsWith("food_")) continue;

      Integer foodId = Integer.valueOf(food.substring(5));
      int qty = Integer.parseInt(paramsMap.getValue());
      if (qty <= 0) continue;

      FoodVO f = foodSvc.getById(foodId);
      BigDecimal price = BigDecimal.valueOf(f.getFoodPrice());
      BigDecimal subtotal =price.multiply(BigDecimal.valueOf(qty));

      OrderDraft.FoodItem item = new OrderDraft.FoodItem();
      item.setFoodId(foodId);
      item.setName(f.getFoodName());
      item.setPrice(price);
      item.setQty(qty);
      item.setSubtotal(subtotal);

      foods.add(item);
      foodTotal=foodTotal.add(subtotal);
    }
    //寫回draft
    draft.setTickets(tickets);
    draft.setFoods(foods);
    draft.setTotalTicketQty(totalQty);
    draft.setTicketTotal(ticketTotal);
    draft.setFoodTotal(foodTotal);
    draft.setTotal(ticketTotal.add(foodTotal));

    //seat開始倒數
    resetExpire(draft);

    return "redirect:/order/seat";
  }

  //5. GET /order/seat :畫座位圖、顯示已售/已鎖住
  @GetMapping("/seat")
  public String seatPage(Model model,@ModelAttribute("orderDraft") OrderDraft draft,SessionStatus sessionStatus) {

    if (draft.getSessionId() == null) return "redirect:/order";
	if (isExpired(draft)) {
		 sessionStatus.setComplete();
		 return "redirect:/order";
	 }

    Integer sessionId = draft.getSessionId();

    //從sessionId反推cinemaId，抓出該影廳所有seats
    SessionVO session = sessionRepo.findById(sessionId).orElseThrow();
    Integer cinemaId = session.getCinema().getCinemaId();   //影廳id
    List<SeatVO> seats = seatSvc.listByCinema(cinemaId);
    
    //組成畫面用的seatMap（按照row分組）
    Map<String, List<SeatVO>> seatMap = new LinkedHashMap<>();

    for (SeatVO seat : seats) {
        String row = seat.getRowNo(); //ex:A B C D E
        
        if (!seatMap.containsKey(row)) {
            seatMap.put(row, new ArrayList<>());//如果這一排第一次出現，先幫它準備一個List
        }
        seatMap.get(row).add(seat);//把目前這顆座位，加進對應排號的List
    }

    model.addAttribute("seatMap", seatMap);

    //0損壞 1可售 2售出
    //壞掉/已售出
    String st = session.getAllSeatStatus();
    char[] status=st.toCharArray();
    List<Integer> unavailableSeatIds = new ArrayList<>();
    for(int i=0 ;i<75 ; i++) {
    	if(status[i] =='0' || status[i]=='2') { //0和2加入unavailableSeatIds
    		unavailableSeatIds.add(seats.get(i).getSeatId());
    	}
    }
    

    
    //被別人鎖走的座位（有排除自己鎖住的座位）
    List<Integer> lockedSeatIds=seatLockSvc.getLockedSeatIds(sessionId, draft.getMemberId(),seats,draft.getLockToken());
    
    //我自己先前已選的座位，要顯示成「已選」橘色
    List<Integer> selectedSeatIds = new ArrayList<>();
    if (draft.getSelectedSeats() != null) {
        for (OrderDraft.SeatSelected ss : draft.getSelectedSeats()) {
            if (ss != null && ss.getSeatId() != null) {
                selectedSeatIds.add(ss.getSeatId());
            }
        }
    }
    
    model.addAttribute("unavailableSeatIds",unavailableSeatIds);
    model.addAttribute("lockedSeatIds",lockedSeatIds);
    model.addAttribute("selectedSeatIds",selectedSeatIds);
    model.addAttribute("expireAt",draft.getExpireAt());
    model.addAttribute("draft",draft);

    return "front-end/order/seat";
  }


  // 6.POST /order/seat/next：使用者選好座位，按「下一步」
  //接收使用者選的座位(body: seatIdsCsv=1,2,3,...)
  //真正去鎖座位：成功才允許進confirm
  @PostMapping("/seat/next")
  public String seatNext(@RequestParam String seatIdsCsv,
                         @ModelAttribute("orderDraft") OrderDraft draft,
                         Model model,
                         SessionStatus sessionStatus,
                         RedirectAttributes ra) {

	  if (isExpired(draft)) {
    	  sessionStatus.setComplete();
    	  return "redirect:/order";
      }
      if (draft.getSessionId() == null) return "redirect:/order";

      //把 "1,2,3" → 拆成 ["1","2","3"] → 轉成整數 1,2,3
      List<Integer> seatIds = new ArrayList<>();
      for (String x : seatIdsCsv.split(",")) {
          if (!x.isBlank()) seatIds.add(Integer.valueOf(x.trim()));
      }

      //驗證座位數要等於票數
      if (seatIds.size() != draft.getTotalTicketQty()) {
          ra.addFlashAttribute("errorMessage", "座位總數量需等於" + draft.getTotalTicketQty() + " 個座位");
          return "redirect:/order/seat";
      }

      //Redis鎖住座位
      //Map<String,Object>：可以回傳很多資訊
      Map<String, Object> lockResult = seatLockSvc.lockSeats(draft.getSessionId(), seatIds, draft.getLockToken(),draft.getMemberId());
      boolean ok = Boolean.TRUE.equals(lockResult.get("success"));//就算是null，也不會炸，只會是false
      List<Integer> unavailableSeatIds = new ArrayList<>();
      
      //鎖位失敗：取出失敗資訊
      if (!ok) {
          Object msg = lockResult.get("message"); //失敗原因

          ra.addFlashAttribute("errorMessage", msg != null ? msg.toString() : "座位已被其他人鎖定，請重新選位");
          model.addAttribute("unavailableSeatIds",unavailableSeatIds);
          return "redirect:/order/seat";
      }

     //鎖位成功：寫進草稿
      List<SeatVO> selectSeats=seatRepo.findAllById(seatIds);
      
      Map<Integer,SeatVO> byId =new HashMap<>();
      for(SeatVO s :selectSeats) {
    	  byId.put(s.getSeatId(), s);
      }
      
      List<OrderDraft.SeatSelected> seatSelected =new ArrayList<>();
      for(Integer seatId :seatIds) {
    	  SeatVO s=byId.get(seatId);
    	  
    	  OrderDraft.SeatSelected seat=new OrderDraft.SeatSelected();
    	  seat.setSeatId(s.getSeatId());
    	  seat.setRow(s.getRowNo());
    	  seat.setSeat(s.getColumnNo());
    	  
    	  seatSelected.add(seat);
      }
      
      //先抓draft裡的舊座位
      List<Integer> oldSeatIds = new ArrayList<>();
      if(draft.getSelectedSeats()!=null) {
    	  for(OrderDraft.SeatSelected s : draft.getSelectedSeats()) {
    		  oldSeatIds.add(s.getSeatId());
    	  }
      }
      //lock新座位成功後：只釋放「舊有但新沒有」的
      if (!oldSeatIds.isEmpty()) {
          List<Integer> toRelease = new ArrayList<>();
          // 找出 oldSeatIds 裡面「不在新 seatIds」的那些
          for (Integer oldId : oldSeatIds) {
              boolean stillSelected = false;
              for (Integer newId : seatIds) {
                  if (oldId != null && oldId.equals(newId)) {
                      stillSelected = true;
                      break;
                  }
              }
              if (!stillSelected) {
                  toRelease.add(oldId);
              }
          }
          //釋放舊鎖（只會刪自己memberId:lockToken的鎖）
          seatLockSvc.releaseSeats(draft.getSessionId(), toRelease, draft.getLockToken(), draft.getMemberId());
      }
      //存回draft
      draft.setSelectedSeats(seatSelected);
      return "redirect:/order/confirm";
  }

  // 7. GET /order/confirm
  @GetMapping("/confirm")
  public String confirm(Model model,@ModelAttribute("orderDraft") OrderDraft draft,SessionStatus sessionStatus) {

    if (isExpired(draft)) {
    	sessionStatus.setComplete();
        return "redirect:/order";
    }
    if (draft.getSelectedSeats() == null || draft.getSelectedSeats().isEmpty()) {
      return "redirect:/order/seat";
    }

    model.addAttribute("expireAt", draft.getExpireAt());
    return "front-end/order/confirm";
  }


  // 8. POST /order/submit：下單成功 → setComplete() → redirect 完成頁
  @PostMapping("/submit")
  public String submit(@ModelAttribute("orderDraft") OrderDraft draft,
                       Model model,
                       SessionStatus sessionStatus,
                       RedirectAttributes ra) {

	  if (isExpired(draft)) {
	    	sessionStatus.setComplete();
	        return "redirect:/order";
	    }
	  
	  Integer memberId = draft.getMemberId();
	  //改去登入頁
//	  if (memberId == null) {
//		  return "redirect:/order";
//	  }

      //檢查訂單草稿的完整性
      if (draft.getMovieId() == null || draft.getDate() == null || draft.getSessionId() == null) {
          return "redirect:/order";
      }
      if (draft.getTickets() == null || draft.getTickets().isEmpty() || draft.getTotalTicketQty() == null || draft.getTotalTicketQty() <= 0) {
          return "redirect:/order";
      }
      if (draft.getSelectedSeats() == null || draft.getSelectedSeats().size() != draft.getTotalTicketQty()) {
          return "redirect:/order/seat";
      }

      //把seatIds整理出來給service、釋放redis用
      List<OrderDraft.SeatSelected> seatSelected = draft.getSelectedSeats();
      List<Integer> seatIds = new ArrayList<>();

      for (int i = 0; i < seatSelected.size(); i++) {
          seatIds.add(seatSelected.get(i).getSeatId());
      }


      try {
          //建立訂單（在service做「交易」+「座位已售出檢查」+「寫入訂單/票券/餐飲」）
          //讓createOrder回傳orderId
          OrderVO order = orderSvc.createOrder(draft,memberId);

          //清掉session的草稿
//          sessionStatus.setComplete();//告訴Spring這個session attribute的流程完成了，要清除

          return "redirect:/payment/gotoecpay?orderId=" + order.getOrderId();

      } catch (Exception e) {
    	  sessionStatus.setComplete();
          ra.addFlashAttribute("errorMessage", "下單失敗，請重新選購");
          return "redirect:/order";
      } 
  }
  
  @GetMapping("/orderresult")
  public String orderQuestionPage() {
      return "front-end/order/orderResult";
  }
}