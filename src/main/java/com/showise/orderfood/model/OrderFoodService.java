package com.showise.orderfood.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.showise.orderticket.model.OrderTicketVO;

@Service
public class OrderFoodService {
	
	@Autowired
	OrderFoodRepository repository;
	
	public void addOrderFood(OrderFoodVO orderFoodVO) {
		repository.save(orderFoodVO);
	}
	
	public OrderFoodVO getOneOrderFood(Integer orderFoodId) {
		Optional<OrderFoodVO> optional = repository.findById(orderFoodId);
		return optional.orElse(null);
	}
	
	

}
