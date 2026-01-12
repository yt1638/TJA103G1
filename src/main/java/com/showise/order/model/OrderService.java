package com.showise.order.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.food.model.FoodRepository;
import com.showise.food.model.FoodVO;
import com.showise.member.model.MemberRepository;
import com.showise.member.model.MemberVO;
import com.showise.movie.model.MovieRepository;
import com.showise.orderfood.model.OrderFoodVO;
import com.showise.orderticket.model.OrderTicketVO;
import com.showise.seat.model.SeatRepository;
import com.showise.seat.model.SeatService;
import com.showise.seat.model.SeatVO;
import com.showise.session.model.SessionRepository;
import com.showise.session.model.SessionVO;
import com.showise.tickettype.model.TicketTypeRepository;
import com.showise.tickettype.model.TicketTypeVO;

@Service
public class OrderService {

	@Autowired
	OrderRepository orderRepo;

	@Autowired
	OrderCompositeNativeSQLRepository orderCompositeNativeSQLRepository;

	@Autowired
	MemberRepository memberRepo;

	@Autowired
	SessionRepository sessionRepo;

	@Autowired
	SeatRepository seatRepo;

	@Autowired
	TicketTypeRepository ticketTypeRepo;

	@Autowired
	FoodRepository foodRepo;
	
	@Autowired
	SeatService seatSvc;
	
	@Autowired
	MovieRepository movieRepo;

	public List<OrderVO> getAll(Map<String, String[]> map) {
		return orderCompositeNativeSQLRepository.getAll(map);
	}

	@Transactional(readOnly = true)
	public OrderVO getDetails(Integer orderId) {
		Optional<OrderVO> optional = orderRepo.findDetailWithTickets(orderId);
		if (optional.isEmpty()) {
			return null; // 先查票券，查不到就回傳null
		}
		OrderVO order = optional.get(); // 拿到查詢的資料
		orderRepo.findDetailWithFoods(orderId); // 再查Foods
		return order;
	}
	
	//把訂單中的每一張票（OrderTicketVO）依票種分組，做成「票種」摘要
	//key是票種名稱
	//value=DTO，裝票種名稱、單價、張數
	public List<TicketSummaryDTO> ticketSummary(OrderVO order) {

		Map<String, TicketSummaryDTO> map = new LinkedHashMap<>();//保留插入順序，前端顯示比較穩定

		if (order == null || order.getOrderTickets() == null) {
			return List.of(); //回傳一個不可修改的空List，表示「沒有資料」
		}
		
		//取出這張票的「票種名稱」與「單價」
		for (OrderTicketVO orderTicket : order.getOrderTickets()) {
			String name = orderTicket.getTicketType().getTicketName();//ticketName:分組依據
			BigDecimal price = orderTicket.getTicketPrice();//下單當下的價格，避免未來票價調整

			TicketSummaryDTO dto = map.get(name);//從map裡找「這個票種的摘要DTO」
			if (dto == null) {
				dto = new TicketSummaryDTO(name, price);//如果map裡沒有這個票種 → new DTO
				map.put(name, dto);
			}
			dto.addOne();//張數+1
		}
		return new ArrayList<>(map.values());//map.values():取出所有 DTO
	}

	// 票券小計
	public BigDecimal calcTicketTotal(OrderVO order) {
		BigDecimal total = BigDecimal.ZERO;
	    
		for(TicketSummaryDTO dto:ticketSummary(order)) {
            BigDecimal subtotal =dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
                total = total.add(subtotal);
		}

		return total;
	}

	// 餐飲小計
	public BigDecimal calcFoodTotal(OrderVO order) {
		BigDecimal total = BigDecimal.ZERO;

		if (order == null || order.getOrderFoods() == null) {
			return total;
		}
		for (OrderFoodVO orderFood : order.getOrderFoods()) {
			if (orderFood.getFoodPrice() != null && orderFood.getFoodQuantity() != null) {
				BigDecimal subTotal = orderFood.getFoodPrice()
						.multiply(BigDecimal.valueOf(orderFood.getFoodQuantity()));
				total = total.add(subTotal);
			}
		}
		return total;
	}

	@Transactional
	public OrderVO createOrder(OrderDraft draft, Integer memberId) {

	    //1.查資料
	    MemberVO member = memberRepo.findById(memberId).orElse(null);
	    if(member == null) {
	    	throw new RuntimeException("沒有這個會員");
	    }

	    SessionVO session = sessionRepo.findById(draft.getSessionId()).orElse(null);
	    if(session == null) {
	    	throw new RuntimeException("沒有這個場次");
	    }

	    List<Integer> seatIds = new ArrayList<>();
	    for(OrderDraft.SeatSelected s : draft.getSelectedSeats()) {
	    	seatIds.add(s.getSeatId());
	    }
	    
	    //檢查DB座位是否可以賣
	    String  st =session.getAllSeatStatus();
	    char[] status =st.toCharArray();
	    Integer cinemaId =session.getCinema().getCinemaId();
	    List<SeatVO> seats=seatSvc.listByCinema(cinemaId);
	    
	    Map<Integer ,Integer> idexBySeatId =new HashMap<>();
	    for(int i = 0;i<seats.size();i++) {
	    	idexBySeatId.put(seats.get(i).getSeatId(),i);
	    }
	    for(Integer seatId : seatIds) {
	    	Integer index =idexBySeatId.get(seatId);
	    	if(status[index] == '0') {
	    		throw new RuntimeException("座位已損壞，seatId="+seatId);
	    	}
	    	if(status[index] == '2') {
	    		throw new RuntimeException("座位已售出，seatId="+seatId);
	    	}
	    }
	    

	    //3.建立待付款訂單
	    OrderVO order = new OrderVO();
	    order.setMember(member);
	    order.setSession(session);
	    order.setOrderStatus(0); // 0待付款
	    order.setLockToken(draft.getLockToken());


	    //4.把票種展開成「每張票一個ticketTypeId」
	    List<Integer> ticketTypeIds = new ArrayList<>();
	    List<BigDecimal> ticketPrices = new ArrayList<>();
	    for (OrderDraft.TicketItem t : draft.getTickets()) {
	        int qty = t.getQty();
	        for (int i = 0; i < qty; i++) {
	        	ticketTypeIds.add(t.getTicketTypeId());
	        	ticketPrices.add(t.getPrice());
	        } 
	    }

	    //5.建票券
	    Set<OrderTicketVO> orderTickets = new HashSet<>();
	    BigDecimal ticketTotal = BigDecimal.ZERO;

	    for (int i = 0; i < seatIds.size(); i++) {

	        SeatVO seat = seatRepo.findById(seatIds.get(i)).orElse(null);
	        if(seat ==null) {
	        	throw new RuntimeException("沒有這個座位");
	        }

	        TicketTypeVO tt = ticketTypeRepo.findById(ticketTypeIds.get(i)).orElse(null);
	        if(tt == null) {
	        	throw new RuntimeException("沒有這個票種");
	        }
	        
	        BigDecimal price = ticketPrices.get(i);

	        OrderTicketVO ot = new OrderTicketVO();
	        ot.setOrder(order);
	        ot.setSeat(seat);
	        ot.setTicketType(tt);
	        ot.setTicketPrice(price);

	        orderTickets.add(ot);
	        ticketTotal = ticketTotal.add(price);
	    }
	    
	    //6.建餐飲
	    Set<OrderFoodVO> orderFoods = new HashSet<>();
	    BigDecimal foodTotal = BigDecimal.ZERO;

	    if (draft.getFoods() != null) {
	        for (OrderDraft.FoodItem f : draft.getFoods()) {
	            FoodVO food = foodRepo.findById(f.getFoodId()).orElse(null);
	            if(food == null) {
	            	throw new RuntimeException("沒有這個餐飲");
	            }
	            int qty = f.getQty();
	            if (qty <= 0) {
	            	continue;
	            }
	            BigDecimal price = BigDecimal.valueOf(food.getFoodPrice());
	            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));

	            OrderFoodVO of = new OrderFoodVO();
	            of.setOrder(order);
	            of.setFood(food);
	            of.setFoodQuantity(qty);
	            of.setFoodPrice(price);
	            orderFoods.add(of);
	            foodTotal = foodTotal.add(subtotal);
	        }
	    }
	    BigDecimal total = ticketTotal.add(foodTotal);
	    order.setOrderTickets(orderTickets);
	    order.setOrderFoods(orderFoods);
	    order.setTotalPrice(total);

	    return orderRepo.save(order);
	}
	

	public Map<Integer, BigDecimal> getFinalPricesByMember(Integer memberId) {
	    BigDecimal discount = BigDecimal.ONE;

	    if (memberId != null) {
	        MemberVO member = memberRepo.findById(memberId).orElse(null);
	        if (member != null && member.getMemberClass() != null && member.getMemberClass().getMemberDiscount() != null) {
	            discount = member.getMemberClass().getMemberDiscount();
	        }
	    }

	    List<TicketTypeVO> list = ticketTypeRepo.findAll();
	    Map<Integer, BigDecimal> map = new HashMap<>();

	    for (TicketTypeVO tt : list) {
	        BigDecimal origin = BigDecimal.valueOf(tt.getTicketPrice()); 
	        BigDecimal finalPrice = origin.multiply(discount).setScale(0, RoundingMode.HALF_UP); //四捨五入
	        map.put(tt.getTicketTypeId(), finalPrice);
	    }
	    return map;
	}
	
	
	public OrderVO updateOrder(OrderVO order) {
        return orderRepo.save(order);
    }

    public OrderVO findOrderByQrCode(String code) {
        return orderRepo.findByQrCode(code);
    }
    
    public List<OrderVO> findOrderToRemind(LocalDateTime from,LocalDateTime to){
		return orderRepo.findOrderToRemind(from, to);
	}


}
