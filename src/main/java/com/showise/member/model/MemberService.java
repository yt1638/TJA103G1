package com.showise.member.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("memberService")
public class MemberService {

	@Autowired
	MemberRepository repository;
	
	public void addMember(MemberVO member) {
		repository.save(member);	
	}
	
	public void updateMember(MemberVO member) {
		repository.save(member);
	}
	
	public void deleteMember(Integer memberId) {
		if(repository.existsById(memberId)) {
			repository.deleteById(memberId);
		}
	}
	
	public MemberVO getOneMember(Integer memberId) {
		Optional<MemberVO> optional = repository.findById(memberId);
		return optional.orElse(null);
	}
	
	public List<MemberVO> getAll(){
		return repository.findAll();
	}
	
	
	
}
