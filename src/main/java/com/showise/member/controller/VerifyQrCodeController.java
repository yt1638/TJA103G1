package com.showise.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.showise.order.model.OrderService;
import com.showise.order.model.OrderVO;

public class VerifyQrCodeController {

	@Autowired
	OrderService orderService;
	
	@GetMapping("/ticket/verify")
	@ResponseBody
	@Transactional		// 避免重複驗票
	public String verifyTicket(@RequestParam("code") String code) {
		
		OrderVO order = orderService.findOrderByQrCode(code);
		
		// QRCode無效
		if (order == null) {
	        return "驗票失敗: 無效 QR Code";
	    }
	
		// 票券已驗過
		if(Boolean.TRUE.equals(order.getUsed())) {
			return "此票券已使用過，請勿重複驗票";
		}
		
		// 尚未驗票，以下標記為已驗過
		order.setUsed(true);
		orderService.updateOrder(order);
		
		return "驗票成功！\n"
        + "訂單編號：" + order.getOrderId() + "\n"
        + "電影：" + order.getSession().getMovie().getNameTw();
	}
}
