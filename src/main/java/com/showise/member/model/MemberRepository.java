package com.showise.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<MemberVO, Integer>{

//	@Query(value = "from Member where memberId=?1 and email like?2 and name like?3 and phone like?4 order by memberId")
//	List<MemberVO> findByOthers(int memberId, String email, String name, String phone);
	
	@Query("select distinct mem from MemberVO mem join fetch mem.memberPreferType p where p.movieType.movieTypeId = :movieTypeId")
	List<MemberVO> findMemberByPrefer(@Param("movieTypeId") Integer movieTypeId);
}
