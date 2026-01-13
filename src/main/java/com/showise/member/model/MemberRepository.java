package com.showise.member.model;

import java.util.List;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<MemberVO, Integer>{
	
	@Query("select distinct mem from MemberVO mem join fetch mem.memberPreferType p where p.movieType.movieTypeId = :movieTypeId")
	List<MemberVO> findMemberByPrefer(@Param("movieTypeId") Integer movieTypeId);

	List<MemberVO> findByNameContaining(String name);

	Optional<MemberVO> findByEmail(String email);
	
	// 查某會員累積消費總額，沒訂單回傳0
	@Query(value = """
	        SELECT COALESCE(SUM(o.total_price), 0)
	        FROM order_info o
	        WHERE o.member_id = :memberId
	    """, nativeQuery = true)
	    Integer sumConsumption(@Param("memberId") Integer memberId);

}
