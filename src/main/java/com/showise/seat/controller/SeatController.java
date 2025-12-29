package com.showise.seat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.showise.cinema.model.CinemaService;
import com.showise.seat.model.SeatService;

@Controller
@RequestMapping("/seat")
public class SeatController {
	
	@Autowired
	SeatService seatService;
	
	@Autowired 
	CinemaService cinemaService;
	
    @GetMapping("/")
    public String seatManager(Model model,@RequestParam(required = false) Integer cinemaId) {
    	model.addAttribute("cinemaList",cinemaService.getAllCinema());
    	if(cinemaId != null) {
    		model.addAttribute("seatList",seatService.listByCinema(cinemaId));
    		model.addAttribute("cinemaId",cinemaId);
    	}
    	return "back-end/seat/manage";
    }
    
    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam(value = "seatId") Integer seatId,Model model,@RequestParam(value = "status") Integer status,@RequestParam(value= "cinemaId") Integer cinemaId) {
    	seatService.updateSeatStatus(seatId, status);
    	return "redirect:/seat/?cinemaId=" + cinemaId;
    }

}
