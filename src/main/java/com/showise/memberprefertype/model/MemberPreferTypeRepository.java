package com.showise.memberprefertype.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferTypeRepository extends JpaRepository<MemberPreferTypeVO, Integer>{

//	@Query(value = "from MemberPreferType where memberPreferTypeId=?1 and member.memberId =?2 order by memberPreferTypeId")
//	List<MemberPreferTypeVO> findByOthers(int memberPreferTypeId, MemberVO member);
}
