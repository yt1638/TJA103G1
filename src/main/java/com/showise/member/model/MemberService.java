package com.showise.member.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.memberclass.model.MemberClassService;
import com.showise.memberclass.model.MemberClassVO;


@Service("memberService")
@Transactional
public class MemberService {

	@Autowired
	MemberRepository repository;
	
	@Autowired
	MemberClassService memberClassService;
	
	
	
	public void addMember(MemberVO member) {
		MemberClassVO defaultClass = memberClassService.getOneMemberClass(1);
		member.setMemberClass(defaultClass);
		repository.save(member);	
	}
	
	
	public void updateMember(MemberVO member) {
		// 若沒有寫以下程式碼，member會是從前端表單綁回來的物件（transient entity），並不是Hibernate管理的實體，資料可能不會正確更新到資料庫
		// 先抓取資料庫裡的會員 (managed entity)	    
	    MemberVO existing = repository.findById(member.getMemberId()).orElse(null);
	    if (existing == null) {
	        throw new RuntimeException("會員不存在: " + member.getMemberId());
	    }
	    
	    // 更新欄位
	    existing.setPassword(member.getPassword());
	    existing.setName(member.getName());
	    existing.setPhone(member.getPhone());
	    existing.setStatus(member.getStatus());
	    existing.setAccConsumption(member.getAccConsumption() != null ? member.getAccConsumption() : 0);
	    
	    // 處理會員等級
	    if (member.getMemberClass() != null && member.getMemberClass().getMemberClassId() != null) {
	        MemberClassVO managedClass = memberClassService.getOneMemberClass(member.getMemberClass().getMemberClassId());
	        existing.setMemberClass(managedClass);
	    }

	    // 儲存更新
	    repository.save(existing); 
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
	

	public List<MemberVO> findMemberByPrefer(Integer movieTypeId){
		return repository.findMemberByPrefer(movieTypeId);
	}
	

	// 根據關鍵字找會員
	public List<MemberVO> findByNameContaining(String keyword) {
	    return repository.findByNameContaining(keyword);
	}

	
	// 登入 
	public MemberVO loginByEmail(String email, String password) {

	    Optional<MemberVO> optional = repository.findByEmail(email);
	    if (optional.isEmpty()) {	// 查無帳號
	        return null; 
	    }

	    MemberVO member = optional.get();
	    // 會員密碼比對
	    if (!member.getPassword().equals(password)) {	// 密碼錯誤
	        return null; 
	    }

	    // 會員帳號狀態檢查
	    if (member.getStatus() != null && member.getStatus() != 0) {	// 非正常狀態或停權(0:啟用/ 1:停用) 
	        return null; 
	    }

	    return member;
	}

	
	// 註冊
	public MemberVO register(MemberVO member) {
		
		// 以下設定一些預設的資料值(會員等級、帳號狀態、累積消費金額)
		// 會員等級
		MemberClassVO defaultClass = memberClassService.getOneMemberClass(1);	// 預設的會員等級是1(一般會員)
		member.setMemberClass(defaultClass);
		
		// 帳號狀態 (0:啟用 1:停用 預設是0)
		member.setStatus(0);			
		
		// 累積消費金額 (預設累積消費金額是0)
		member.setAccConsumption(0);	
		
		MemberVO savedMember = repository.save(member);
		
		
		if (savedMember.getMemberId() == null) {
		    throw new RuntimeException("會員註冊失敗，memberId 為 null");
		}
		
		System.out.println("會員 " + savedMember.getMemberId() + " 註冊成功");
		return savedMember;				
	}
	
	
	// 檢查email是否已經存在
	public boolean existsByEmail(String email) {
		return repository.findByEmail(email).isPresent();
	}
	
	
	// 更新密碼
	@Transactional
	public void updatePassword(String email, String password) {
		
		Optional<MemberVO> optionalMember = repository.findByEmail(email);

        if (!optionalMember.isPresent()) {
            throw new RuntimeException("會員不存在");
        }

        MemberVO member = optionalMember.get();
        member.setPassword(password);
        repository.save(member);
	}

}
