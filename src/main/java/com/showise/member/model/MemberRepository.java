package com.showise.member.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberVO, Integer>{

//	@Query(value = "from Member where memberId=?1 and email like?2 and name like?3 and phone like?4 order by memberId")
//	List<MemberVO> findByOthers(int memberId, String email, String name, String phone);
}
