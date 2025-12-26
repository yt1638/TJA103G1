package com.showise.order.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.showise.member.model.MemberVO;

@Service
public class OrderService {
	
	@Autowired
	OrderRepository repository;
	
	public void addOrder(OrderVO order) {
		repository.save(order);	
	}
	
	public OrderVO getOneOrder(Integer orderId) {
		Optional<OrderVO> optional = repository.findById(orderId);
		return optional.orElse(null);
	}
	
	public List<OrderVO> getAll() {
		return repository.findAll();
	}
	

    
}
