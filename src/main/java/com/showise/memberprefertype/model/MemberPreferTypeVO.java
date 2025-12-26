package com.showise.memberprefertype.model;

import java.io.Serializable;

import com.showise.member.model.MemberVO;
import com.showise.movietype.model.MovieTypeVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "member_prefer_type")
public class MemberPreferTypeVO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_prefer_type_id", updatable = false)
	private Integer memberPreferTypeId;
	
	
//	關聯
//	電影編號
	@ManyToOne
	@JoinColumn(name = "movie_type_id", referencedColumnName = "movie_type_id")
	private MovieTypeVO movieType;
	
	
//	會員編號
	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private MemberVO member;
	
	
	public MemberPreferTypeVO() {
		super();
	}

	public Integer getMemberPreferTypeId() {
		return memberPreferTypeId;
	}

	public void setMemberPreferTypeId(Integer memberPreferTypeId) {
		this.memberPreferTypeId = memberPreferTypeId;
	}

	public MovieTypeVO getMovieType() {
		return movieType;
	}

	public void setMovieType(MovieTypeVO movieType) {
		this.movieType = movieType;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

}
