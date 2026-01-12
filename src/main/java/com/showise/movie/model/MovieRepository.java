package com.showise.movie.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.showise.order.model.OrderVO;

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
	
	@Query("from MovieVO m order by m.status asc,m.movieId desc ")
	List<MovieVO> findAllOrderByStatusAndMovieId();
	
	@Query(value = "from MovieVO m where ?1 >= m.releaseDate AND ?1<=m.endDate AND m.status=1")
	List<MovieVO> findByDate(LocalDate searchDate);
	
	@Query(value = "select distinct m from MovieVO m join fetch m.eachMovieTypes emt join fetch emt.movieType where m.status = ?1 and emt.movieType.movieTypeId = ?2")
	List<MovieVO> findByStatusAndType(Integer status,Integer movieTypeId);
	
	@Query("SELECT DISTINCT m FROM MovieVO m WHERE m.sented = false AND " +
	           "m.releaseDate = :releaseDate")
	    List<MovieVO> findMovieToRemind(
	            @Param("releaseDate") LocalDate releaseDate
	    );


}
