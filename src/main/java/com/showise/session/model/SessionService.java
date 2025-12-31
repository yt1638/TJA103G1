package com.showise.session.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.cinema.model.CinemaService;
import com.showise.movie.model.MovieService;
import com.showise.movie.model.MovieVO;
import com.showise.order.model.OrderRepository;
import com.showise.seat.model.SeatService;

@Service
public class SessionService {
	
	@Autowired 
	SessionRepository repository;
	@Autowired 
	MovieService movieService;
	@Autowired 
	CinemaService cinemaService;
	@Autowired
	SeatService seatService;
	@Autowired
	OrderRepository orderRepository;
	
	@Transactional
	public void addSession(LocalDate searchDate,Integer movieId,Integer cinemaId,String startTimeStr) {
		MovieVO movieVO = movieService.getById(movieId);
		LocalTime startTime = LocalTime.parse(startTimeStr);
		LocalDateTime startDateTime = LocalDateTime.of(searchDate, startTime);
		LocalDateTime endDateTime = startDateTime.plusMinutes(movieVO.getDuration()+20);
		
		Long conflict = repository.sessionConflict(cinemaId, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime));
		
		if(conflict > 0) {
			throw new IllegalArgumentException("ADDCONFLICT");
		}
		String initialStatus = seatService.listSeatStatusByCinema(cinemaId);
		SessionVO sessionVO = new SessionVO();
		sessionVO.setSessionStatus(1);
		sessionVO.setAllSeatStatus(initialStatus);
		sessionVO.setCinema(cinemaService.getById(cinemaId));
		sessionVO.setMovie(movieVO);
		sessionVO.setStartTime(Timestamp.valueOf(startDateTime));
		sessionVO.setEndTime(Timestamp.valueOf(endDateTime));
		repository.save(sessionVO);
	}
	@Transactional
	public void toggleStatus(Integer sessionId) {
		SessionVO sessionVO = repository.findById(sessionId).orElseThrow(() -> new RuntimeException("場次不存在"));
		if(orderRepository.existsBySession_SessionIdAndOrderStatus(sessionId, 1)) {
			throw new IllegalArgumentException("Ordered");
		}else {
		sessionVO.setSessionStatus(sessionVO.getSessionStatus() == 1?0:1);
		repository.save(sessionVO);
		}
	}
	
	@Transactional
	public void updateSessionTime(Integer sessionId,String newStartTimeStr) {
		SessionVO sessionVO = repository.findById(sessionId).orElseThrow(() -> new RuntimeException("查無場次"));
		LocalTime startTime = LocalTime.parse(newStartTimeStr);
		
		Integer cinemaId = sessionVO.getCinema().getCinemaId();
		
		LocalDate date = sessionVO.getStartTime().toLocalDateTime().toLocalDate();
		LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
		LocalDateTime endDateTime = startDateTime.plusMinutes(sessionVO.getMovie().getDuration()+20);
		
		long conflict = repository.sessionConflictExcludingSelf(cinemaId, Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), sessionId);
		
		if(conflict>0) {
			throw new IllegalArgumentException("UPDATECONFLICT");
		}
		sessionVO.setStartTime(Timestamp.valueOf(startDateTime));
		sessionVO.setEndTime(Timestamp.valueOf(endDateTime));
		if(orderRepository.existsBySession_SessionIdAndOrderStatus(sessionId, 1)) {
			throw new IllegalStateException("Ordered");
		}else {
			repository.save(sessionVO);
		}
	}
		
	
	public List<SessionVO> listByMovieId(Integer movieId) {
		return repository.listByMovieId(movieId);
	}
	
	public List<SessionVO> listByDate(LocalDate date){
		Timestamp start = Timestamp.valueOf(date.atStartOfDay());
		Timestamp end = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
		return repository.listByDate(start, end);
	}
	
	public List<SessionVO> listAll(){
		return repository.findAll();
	}
	
	public SessionVO getById(Integer sessionId) {
		Optional<SessionVO> optional = repository.findById(sessionId);
		return optional.orElse(null);
	}
	
	public List<SessionVO> listByCinema(Integer cinemaId){
		return repository.listByCinema(cinemaId);
	}
}
