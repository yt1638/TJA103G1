package com.showise.orderticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.showise.orderticket.model.OrderTicketService;

@Controller
public class OrderTicketController {
	
	@Autowired
	OrderTicketService orderTcicketService;

}
