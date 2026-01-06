package com.showise.order.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.member.model.MemberVO;
import com.showise.orderfood.model.OrderFoodVO;
import com.showise.orderticket.model.OrderTicketVO;

@Service
public class OrderService {
	
	@Autowired
	OrderRepository orderRepo;
	
	@Autowired
	OrderCompositeNativeSQLRepository orderCompositeNativeSQLRepository;
	
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

	// 票券總金額
	public BigDecimal calcTicketTotal(OrderVO order) {
		BigDecimal total = BigDecimal.ZERO;
	    
		for(TicketSummaryDTO dto:ticketSummary(order)) {
            BigDecimal subtotal =dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
                total = total.add(subtotal);
		}

		return total;
	}

	// 餐飲總金額
	public BigDecimal calcFoodTotal(OrderVO order) {
		BigDecimal total = BigDecimal.ZERO;

		if (order == null || order.getOrderFoods() == null) {
			return total;
		}
		for (OrderFoodVO orderFood : order.getOrderFoods()) {
			if (orderFood.getFoodPrice() != null && orderFood.getFoodQuantity() != null) {
				BigDecimal subTotal = orderFood.getFoodPrice().multiply(BigDecimal.valueOf(orderFood.getFoodQuantity()));
				total = total.add(subTotal);
			}
		}
		return total;
	}
	

    
}
