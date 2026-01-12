package com.showise.session.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	
	public List<SessionVO> listByMovieAndDate(Integer movieId,LocalDate date){
		Timestamp start = Timestamp.valueOf(date.atStartOfDay());
		Timestamp end = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
		return repository.listByMovieAndDate(movieId, start, end);
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
	
    public List<MovieVO> listBookableMoviesNext7Days() {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        Timestamp end = Timestamp.valueOf(LocalDate.now().plusDays(7).atStartOfDay());
        List<MovieVO> movies = repository.findBookableMovies(now, end);
        Collections.sort(movies, new Comparator<MovieVO>() {
            @Override
            public int compare(MovieVO m1, MovieVO m2) {
                return m1.getMovieId().compareTo(m2.getMovieId());
            }
        });
        return movies;
    }


    public List<LocalDate> listDatesByMovieNext7Days(Integer movieId) {
        LocalDate today = LocalDate.now();// 用系統預設時區，取得今天
        Timestamp now = Timestamp.valueOf(java.time.LocalDateTime.now());//取得「現在時間」
        Timestamp end = Timestamp.valueOf(today.plusDays(7).atStartOfDay()); // 含今天共7天

        // 先從DB撈出場次時間
        List<Timestamp> startTimes =repository.findStartTimesByMovieInRange(movieId, now, end);

        // 用Set去重（同一天只留一次）
        Set<LocalDate> dateSet = new HashSet<>();
        for (Timestamp ts : startTimes) {
            LocalDate date = ts.toLocalDateTime().toLocalDate();
            dateSet.add(date);
        }
        // 轉成List並排序
        List<LocalDate> result = new ArrayList<>(dateSet);
        Collections.sort(result);
        return result; 
    }


    public List<SessionVO> listSessionsByMovieAndDate(Integer movieId, LocalDate date) {

        Timestamp end = Timestamp.valueOf(date.plusDays(1).atStartOfDay()); // 隔天 00:00

        Timestamp start;
        if (date != null && date.equals(LocalDate.now())) {
            start = Timestamp.valueOf(LocalDateTime.now()); // 今天：從現在開始
        } else {
            start = Timestamp.valueOf(date.atStartOfDay()); // 非今天：從當天 00:00
        }

        return repository.findOnSaleSessionsByMovieAndDay(movieId, start, end);
    }
}
