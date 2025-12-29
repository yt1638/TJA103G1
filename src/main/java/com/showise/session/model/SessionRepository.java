package com.showise.session.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface SessionRepository extends JpaRepository<SessionVO,Integer>{
	
	@Query("select count(s) from SessionVO s where s.cinema.cinemaId = ?1 "
		+  "AND s.startTime <?3"
		+  "AND s.endTime >?2"
		+  "AND s.sessionStatus = 1")
	Long sessionConflict(Integer cinemaId,Timestamp startTime,Timestamp endTime);
	
	@Query("select count(s) from SessionVO s where s.cinema.cinemaId = ?1 " 
		  +"AND s.startTime < ?3 AND s.endTime > ?2 " 
		  +"AND s.sessionStatus = 1 AND s.sessionId != ?4")
	Long sessionConflictExcludingSelf(Integer cinemaId, Timestamp startTime, Timestamp endTime, Integer SessionId);
	@Query("from SessionVO s join fetch s.movie join fetch s.cinema where s.movie.movieId = ?1 order by s.startTime ASC")
	List<SessionVO> listByMovieId(Integer movieId);
	@Query("from SessionVO s join fetch s.movie join fetch s.cinema where s.startTime>=?1 AND s.startTime<=?2 order by s.startTime ASC")
	List<SessionVO> listByDate(Timestamp start,Timestamp end);
	


}
