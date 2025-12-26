package com.showise.memberclass.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.showise.member.model.MemberVO;

@Service("memberClassService")
public class MemberClassService {

	@Autowired
	MemberClassRepository repository;
	
	public void addMemberClass(MemberClassVO memberClass) {
		repository.save(memberClass);
	}
	
	public void updateMemberClass(MemberClassVO memberClass) {
		repository.save(memberClass);
	}
	
	public void deleteMemberClass(Integer memberClassId) {
		if(repository.existsById(memberClassId)) {
			repository.deleteById(memberClassId);
		}
	}
	
	public MemberClassVO getOneMemberClass(Integer memberClassId) {
		Optional<MemberClassVO> optional = repository.findById(memberClassId);
		return optional.orElse(null);
	}
	
	public List<MemberClassVO> getAll(){
		return repository.findAll();
	}
	
	public Set<MemberVO> getMemberByMemberClassId(Integer memberClassId){
		return getOneMemberClass(memberClassId).getMember();
	}
	
}
