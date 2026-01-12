package com.showise.memberprefertype.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.member.model.MemberVO;
import com.showise.movietype.model.MovieTypeRepository;
import com.showise.movietype.model.MovieTypeVO;


@Service("memberPreferTypeService")
public class MemberPreferTypeService {

	@Autowired
	MemberPreferTypeRepository repository;
	
	@Autowired
	private MovieTypeRepository movieTypeRepo;
	
	
	// 刪除某會員的所有會員喜好電影類型
    public void deleteByMember(MemberVO member) {
        repository.deleteByMember(member);
    }
	
    @Transactional
	public void saveMemberPreferTypes(MemberVO member, List<Integer>styleIds) {
		
		// 註冊時用不到，用於「使用者之後編輯」喜好電影風格時
		repository.deleteByMember(member);
		
		// 使用者可能全部取消勾選(避免因為null，再繼續跑下面程式會報例外)
	    if (styleIds == null || styleIds.isEmpty()) {
	        return;
	    }
		
		for(int i = 0; i < styleIds.size(); i++) {
			Integer typeId = styleIds.get(i);
			
			Optional<MovieTypeVO> type = movieTypeRepo.findById(typeId);
			
			// 判斷有沒有查到資料(防止資料庫該筆電影類型被刪掉)
			if(!type.isPresent()) {
				continue;
			}
			
			// Optional.get()是用來取得Optional裡包住的實際物件，只要在呼叫前有先用isPresent()確認裡面有值，就可以安全呼叫 get()，不會拋出例外。
			MovieTypeVO movieType = type.get();
			MemberPreferTypeVO memberPreferTypeVO = new MemberPreferTypeVO();
			memberPreferTypeVO.setMember(member);
			memberPreferTypeVO.setMovieType(movieType);
			
			repository.save(memberPreferTypeVO);
		}
	}
	
	
	
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
	
	public List<MemberPreferTypeVO> getByMemberId(Integer memberId){
		return repository.findByMember_MemberId(memberId);
	}
	
	public List<MemberPreferTypeVO> getByMovieTypeId(Integer movieTypeId){
		return repository.findByMovieType_MovieTypeId(movieTypeId);
	}
	
}
