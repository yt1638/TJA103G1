package com.showise.session.model;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<SessionVO,Integer>{
	
	@Query("select count(s) from SessionVO s where s.cinema.cinemaId = :cinemaId "
		    + "AND s.startTime < :endTime "
		    + "AND s.endTime > :startTime "
		    + "AND s.sessionStatus = 1")
		Long sessionConflict(
		    @Param("cinemaId") Integer cinemaId, 
		    @Param("startTime") Timestamp startTime, 
		    @Param("endTime") Timestamp endTime);

		@Query("select count(s) from SessionVO s where s.cinema.cinemaId = :cinemaId " 
		    + "AND s.startTime < :endTime AND s.endTime > :startTime " 
		    + "AND s.sessionStatus = 1 AND s.sessionId != :sessionId")
		Long sessionConflictExcludingSelf(
		    @Param("cinemaId") Integer cinemaId, 
		    @Param("startTime") Timestamp startTime, 
		    @Param("endTime") Timestamp endTime, 
		    @Param("sessionId") Integer sessionId);

		@Query("from SessionVO s join fetch s.movie join fetch s.cinema where s.movie.movieId = :movieId order by s.startTime ASC")
		List<SessionVO> listByMovieId(@Param("movieId") Integer movieId);

		@Query("from SessionVO s join fetch s.movie join fetch s.cinema where s.startTime >= :start AND s.startTime <= :end order by s.cinema.cinemaId,s.startTime ASC")
		List<SessionVO> listByDate(@Param("start") Timestamp start, @Param("end") Timestamp end);
        
		@Query("from SessionVO s join fetch s.cinema where s.cinema.cinemaId = :cinemaId")
		List<SessionVO> listByCinema(@Param("cinemaId") Integer cinemaId);
		
		@Query("from SessionVO s join fetch s.movie where s.movie.movieId = :movieId and s.startTime >= :start AND s.startTime <= :end order by s.startTime")
		List<SessionVO> listByMovieAndDate(@Param("movieId") Integer movieId,@Param("start") Timestamp start,@Param("end") Timestamp end);

}
