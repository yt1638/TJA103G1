package com.showise.seat.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface seatrepository extends JpaRepository{
	@Query("select distinct s from SeatVO s " +
		       "join fetch s.cinema c  " +
		       "where c.cinemaId= ?1 order by s.seatId desc")
	List<SeatVO> listByCinema();

}
