package com.showise.seat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.showise.seat.model.SeatService;

@Controller
@RequestMapping("/seat")
public class SeatController {
	
	@Autowired
	SeatService seatService;
	
//	@GetMapping("/listBy")

}
