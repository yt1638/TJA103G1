package com.showise.orderticket.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.showise.order.model.OrderVO;

@Service
public class OrderTicketService {
	
	@Autowired
	OrderTicketRepository repository;
	
	public void addOrderTicket(OrderTicketVO orderTicketVO) {
		repository.save(orderTicketVO);
	}
	
	public OrderTicketVO getOneOrderTicket(Integer orderTicketId) {
		Optional<OrderTicketVO> optional = repository.findById(orderTicketId);
		return optional.orElse(null);
	}
	

}
