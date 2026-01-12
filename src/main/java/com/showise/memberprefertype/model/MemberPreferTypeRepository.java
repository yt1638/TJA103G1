package com.showise.memberprefertype.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.showise.member.model.MemberVO;

public interface MemberPreferTypeRepository extends JpaRepository<MemberPreferTypeVO, Integer>{

	// 依照會員編號查詢
	List<MemberPreferTypeVO> findByMember_MemberId(Integer memberId);
	
	// 依照電影類型編號查詢
	List<MemberPreferTypeVO> findByMovieType_MovieTypeId(Integer movieTypeId);
	
	// 刪除某會員全部喜好
	void deleteByMember(MemberVO member);
	
}
