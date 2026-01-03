package com.showise.orderticket.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTicketRepository extends JpaRepository<OrderTicketVO, Integer>{
	boolean existsByOrder_Session_SessionIdAndSeatId(Integer sessionId, Integer seatId);

}
