package com.showise.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.showise.order.model.OrderService;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	OrderService orderServce;

}
