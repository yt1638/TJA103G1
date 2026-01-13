package com.showise.memberclass.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberClassRepository extends JpaRepository<MemberClassVO, Integer> {

	// 依 memberName 查會員等級
    MemberClassVO findByMemberName(String memberName);
}
