package com.showise.order.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderVO, Integer>{
	
	 boolean existsBySession_SessionIdAndOrderStatus(Integer sessionId,Integer orderStatus);
	 
//	 List<OrderVO> findByMemberId(Integer memberId);

}
