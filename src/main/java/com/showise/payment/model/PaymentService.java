package com.showise.payment.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.order.model.OrderRepository;
import com.showise.order.model.OrderVO;
import com.showise.order.model.SeatLockService;
import com.showise.ordermail.model.OrderMailService;
import com.showise.orderticket.model.OrderTicketVO;
import com.showise.seat.model.SeatService;
import com.showise.seat.model.SeatVO;
import com.showise.session.model.SessionRepository;
import com.showise.session.model.SessionVO;

@Service
public class PaymentService {
	@Autowired
    private OrderRepository orderRepo;
	
	@Autowired
	private SessionRepository sessionRepo;
	
	@Autowired
    private SeatService seatSvc;
	
	@Autowired
    private SeatLockService seatLockSvc;
	
	@Autowired
	private OrderMailService mailSvc;

    @Transactional
    public void orderPaid(Integer orderId) {
    	System.out.println("orderPaid"+orderId);
        OrderVO order = orderRepo.findById(orderId).orElse(null);
        if(order==null) {
        	throw new RuntimeException("沒有這筆訂單");
        }

        //1.預防重複通知（notify可能打很多次）
        if (order.getOrderStatus() == 1 ||order.getOrderStatus() == 2) {
        	 return;
        }

        //2.取出seatIds
        List<Integer> seatIds = new ArrayList<>();
        for(OrderTicketVO ot : order.getOrderTickets()) {
        	seatIds.add(ot.getSeat().getSeatId());
        }

        Integer sessionId = order.getSession().getSessionId();

        //4.真正售出：更新allSeatStatus
	    SessionVO session = sessionRepo.findById(sessionId).orElse(null);
	    if(session == null) {
	    	throw new RuntimeException("沒有這個場次");
	    }
	    
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
	    	status[index] ='2';
	    }
	    Integer memberId=order.getMember().getMemberId();
	    String lockToken =order.getLockToken();
        
        String newStatus=new String(status);
        session.setAllSeatStatus(newStatus);
        sessionRepo.save(session);

        //訂單狀態 改成 1已付款
        order.setOrderStatus(1);
        orderRepo.save(order);
        seatLockSvc.releaseSeats(sessionId, seatIds,lockToken,memberId);
        
        try {
        	mailSvc.sendPaymentSuccessMail(order); //發送訂單確認信
        }catch(Exception e) {
        	e.printStackTrace();
        	System.out.println("email沒寄出去");
        }
    }

    @Transactional
    public void orderCancell(Integer orderId) {
    	
        OrderVO order = orderRepo.findById(orderId).orElse(null);
        if(order==null) {
        	throw new RuntimeException("沒有這筆訂單");
        }

        if (order.getOrderStatus() == 1 ||order.getOrderStatus() == 2) {
       	 return;
       }
        List<Integer> seatIds = new ArrayList<>();
        for(OrderTicketVO ot : order.getOrderTickets()) {
        	seatIds.add(ot.getSeat().getSeatId());
        }
        Integer sessionId = order.getSession().getSessionId();
	    Integer memberId=order.getMember().getMemberId();
	    String lockToken =order.getLockToken();

        order.setOrderStatus(2); //2 已取消（包含失敗與逾時）
        orderRepo.save(order);
        seatLockSvc.releaseSeats(sessionId, seatIds,lockToken,memberId);
    }
}
