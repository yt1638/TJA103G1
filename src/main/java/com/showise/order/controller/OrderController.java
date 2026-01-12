package com.showise.order.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.showise.order.model.OrderService;
import com.showise.order.model.OrderVO;
import com.showise.order.model.TicketSummaryDTO;

import jakarta.servlet.http.HttpServletRequest;
//這隻主要是後台的order管理部分

@Controller
@RequestMapping("backend/order")
public class OrderController {

	@Autowired
	OrderService orderSvc;
	
	@GetMapping("/")
	public String orderSearch(Model model) {
		return "back-end/layout/admin-layout";
	}

	@PostMapping("listOrders_ByCompositeNativeSQLQuery")
	public String listOrdersByCompositeNativeSQLQuery(HttpServletRequest req, Model model) {
		
		List<String> errorMessage = new ArrayList<>();
		
		// 錯誤驗證
		String orderId = req.getParameter("orderId");
		if(orderId !=null && !orderId.isBlank()) {
			try {
				Integer.parseInt(orderId);
			}catch(NumberFormatException ne) {
				errorMessage.add("訂單編號必須是數字");
			}
		}	
		String memberId = req.getParameter("memberId");
		if(memberId !=null && !memberId.isBlank()) {
			try {
				Integer.parseInt(memberId);
			}catch(NumberFormatException ne) {
				errorMessage.add("會員編號必須是數字");
			}
		}
				
		String startDateStr = req.getParameter("startDate");
		String endDateStr   = req.getParameter("endDate");

		java.sql.Date startDate = null;
		java.sql.Date endDate = null;

		try {
			if (startDateStr != null && !startDateStr.isBlank()) {
		        startDate = java.sql.Date.valueOf(startDateStr.trim());
		    }
		    if (endDateStr != null && !endDateStr.isBlank()) {
		        endDate = java.sql.Date.valueOf(endDateStr.trim());
		    }
		    if(startDate !=null && endDate != null && startDate.after(endDate)) {
				errorMessage.add("起始日期不可大於結束日期");
			}
		}catch(IllegalArgumentException ie) {
			errorMessage.add("日期格式有錯誤");
		}
		
		// 有錯誤，就回畫面顯示
		if(!errorMessage.isEmpty()) {
			model.addAttribute("errorMessage", errorMessage);
			return "back-end/layout/admin-layout";
		}
		
		Map<String, String[]> map = req.getParameterMap();
		List<OrderVO> list = orderSvc.getAll(map);
		model.addAttribute("orderListData", list); 
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/OrderDetail")
	public String orderDetail(@RequestParam Integer orderId, Model model) {

	    OrderVO order = orderSvc.getDetails(orderId);
	    
		if(order == null) {
			model.addAttribute("errorMessage", "查無此訂單");
			return "back-end/layout/admin-layout";
		}
		
		List<TicketSummaryDTO> ticketSummary = orderSvc.ticketSummary(order);
		BigDecimal ticketTotal = orderSvc.calcTicketTotal(order);
		BigDecimal foodTotal = orderSvc.calcFoodTotal(order);
		
	    
	    model.addAttribute("order", order);
	    model.addAttribute("ticketSummary", ticketSummary);
	    model.addAttribute("ticketTotal", ticketTotal);
	    model.addAttribute("foodTotal", foodTotal);

	    return "back-end/order/orderDetail";
	}
	
	


}
