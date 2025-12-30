package com.showise.seat.model;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {
	
	@Autowired
	SeatRepository repository;
	
	public List<SeatVO> listByCinema(Integer cinemaId){
		return repository.listByCinema(cinemaId);
	}
	@Transactional
	public void updateSeatStatus(Integer seatId,Integer status) {
		SeatVO seatVO = repository.findById(seatId)
			    .orElseThrow(() -> new RuntimeException("Seat 不存在，id = " + seatId));
		seatVO.setSeatStatus(status);
	}
}
