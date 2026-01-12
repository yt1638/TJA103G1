package com.showise.message.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.showise.order.model.OrderService;
import com.showise.order.model.OrderVO;

import jakarta.transaction.Transactional;
@Service
@Transactional
public class SessionRemindScheduler {
	
	@Autowired 
	OrderService ordService;
	
	@Autowired
	MessageService msgService;
	
	@Autowired
	MailService mailService;
	
	@Scheduled(fixedRate = 60000)
	public void sendSessionRemind() {
		
		Integer preHours = msgService.findByType(0).getPreHours();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = now.plusHours(preHours).minusSeconds(30);
		LocalDateTime to = now.plusHours(preHours).plusSeconds(30);
		
		List<OrderVO> orderList = ordService.findOrderToRemind(from, to);
		
		if(orderList.isEmpty()) {
			return;
		}
		
		MessageVO template = msgService.findByType(0);
		
		for(OrderVO order : orderList) {
			mailService.sendSessionRemindMail(template, order);
		}
		
	}

}
