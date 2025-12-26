package com.showise.order.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderVO, Integer>{
	
//	List<OrderVO> findByMemberId(Integer memberId);
	

}
