package com.showise.movie.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieRepository extends JpaRepository<MovieVO,Integer>{
	
	@Query(value = "from MovieVO where nameTw like %?1% order by movieId desc")
	List<MovieVO> findByMovieName(String nameTw);
	@Query(value = "from MovieVO where status = ?1 order by movieId desc")
	List<MovieVO> findByStatus(Integer status);
	@Query("SELECT DISTINCT m FROM MovieVO m " +
		       "JOIN FETCH m.eachMovieTypes emt " +
		       "JOIN FETCH emt.movieType " +
		       "WHERE emt.movieType.movieTypeId = ?1 order by movieId desc")
	List<MovieVO> findByType(Integer movieTypeId);
	
	List<MovieVO> findAllByOrderByMovieIdDesc();


}
