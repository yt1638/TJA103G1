package com.showise.order.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderVO, Integer>{
	
	 //某場次是否已經有已付款的訂單
	 boolean existsBySession_SessionIdAndOrderStatus(Integer sessionId,Integer orderStatus);
	
	 //JPQL
	 //Join fetch:解決 N+1 查詢問題，避免在 service/controller 層拿到lazy關聯時（session 已關閉）。
	 //查訂單時，順便把關聯物件在同一個查詢中載入（一次載入）
	 //distinct o 是告訴 JPA：把重複的root entity合併成一個 OrderVO
	 
	@Query(
		    "select distinct o " +
		    "from OrderVO o " +
		    "left join fetch o.member " +
		    "left join fetch o.session s " +
		    "left join fetch s.cinema " +
		    "left join fetch s.movie " +
		    "left join fetch o.orderTickets ot " +
		    "left join fetch ot.seat " +
		    "left join fetch ot.ticketType " +
		    "where o.orderId = :orderId"
		)
		Optional<OrderVO> findDetailWithTickets(@Param("orderId") Integer orderId);
	//Optional的意義:這個查詢「可能查不到」。查得到：回傳Optional.of(order)。查不到：回傳Optional.empty()
	//好處避免你拿到null

	
	@Query(
		    "select distinct o " +
		    "from OrderVO o " +
		    "left join fetch o.orderFoods of " +
		    "left join fetch of.food f " +
		    "left join fetch f.foodCate " +
		    "where o.orderId = :orderId"
		)
		Optional<OrderVO> findDetailWithFoods(@Param("orderId") Integer orderId);
	
	@Query(
			"select distinct o " +
	        "from OrderVO o " +
			"join fetch o.session s " +
	        "join fetch s.movie m " +
			"join fetch o.member mem " +
	        "where o.sented = false " +
			"and s.startTime between :from and :to"
			)
	    List<OrderVO> findOrderToRemind(@Param("from") LocalDateTime from,@Param("to") LocalDateTime to);

	// 根據 qrCode 查詢訂單
    OrderVO findByQrCode(String qrCode);
    
	

}
