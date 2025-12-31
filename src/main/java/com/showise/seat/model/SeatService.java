package com.showise.seat.model;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.order.model.OrderRepository;
import com.showise.session.model.SessionRepository;
import com.showise.session.model.SessionVO;

@Service
public class SeatService {
	
	@Autowired
	SeatRepository seatRepository;
	@Autowired
	SessionRepository  sessionRepository;
	
	
	public List<SeatVO> listByCinema(Integer cinemaId){
		return seatRepository.listByCinema(cinemaId);
	}
	@Transactional
	public void updateSeatStatus(Integer seatId,Integer status) {
		SeatVO seatVO = seatRepository.findById(seatId)
			    .orElseThrow(() -> new RuntimeException("Seat 不存在，id = " + seatId));
		seatVO.setSeatStatus(status);
		
		Integer cinemaId = (seatId-1)/75+1;
		Integer index = (seatId-1)%75;
		
		List<SessionVO> sessions = sessionRepository.listByCinema(cinemaId);
		
		for(SessionVO session : sessions) {
			char[] allSeatStatus = session.getAllSeatStatus().toCharArray();
			
			if(status == 0) {
				allSeatStatus[index] = '0';
			}else {
				if(allSeatStatus[index] == '2') {
				}else {
					allSeatStatus[index] ='1';
				}
			}
			session.setAllSeatStatus(new String(allSeatStatus));
		}
	}
	public String listSeatStatusByCinema(Integer cinemaId) {
		List<SeatVO> seats = seatRepository.listByCinema(cinemaId);
		StringBuilder allSeatStatusByCinema = new StringBuilder();
		for(SeatVO seat : seats) {
			allSeatStatusByCinema.append(seat.getSeatStatus());
		}
		return allSeatStatusByCinema.toString();
	}
}
