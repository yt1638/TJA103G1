package com.showise.order.model;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	private SessionFactory sessionFactory;

    
}
