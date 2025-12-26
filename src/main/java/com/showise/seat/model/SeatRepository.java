package com.showise.seat.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SeatRepository extends JpaRepository<SeatVO,Integer>{
	@Query("""
		    select s
		    from SeatVO s
		    where s.cinema.cinemaId = :cinemaId
		    order by s.row asc, s.column asc
		""")
	List<SeatVO> listByCinema(Integer cinemaId);
}
