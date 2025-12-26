package com.showise.memberprefertype.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("memberPreferTypeService")
public class MemberPreferTypeService {

	@Autowired
	MemberPreferTypeRepository repository;
	
	public void addMemberPreferType(MemberPreferTypeVO memberPreferType) {
		repository.save(memberPreferType);
	}
	
	public void updateMemberPreferType(MemberPreferTypeVO memberPreferType) {
		repository.save(memberPreferType);
	}
	
	public void deleteMemberPreferType(Integer MemberPreferTypeId) {
		if(repository.existsById(MemberPreferTypeId)) {
			repository.deleteById(MemberPreferTypeId);
		}
	}
	
	public MemberPreferTypeVO getOneMemberPreferType(Integer memberPreferTypeId) {
		Optional<MemberPreferTypeVO> optional = repository.findById(memberPreferTypeId);
		return optional.orElse(null);
	}
	
	public List<MemberPreferTypeVO> getAll(){
		return repository.findAll();
	}
	
}
