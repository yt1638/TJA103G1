package com.showise.memberclass.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import com.showise.member.model.MemberVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;



@Entity
@Table(name = "member_class")
public class MemberClassVO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_class_id", updatable = false)
	private Integer memberClassId;
	
	
	@Column(name = "member_name")
	@NotEmpty(message = "會員等級名稱: 請勿空白")
	@Pattern(regexp = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "姓名: 只能是中文、英文字母、數字和_ , 且長度必需在2到10之間")
	private String memberName;
	
	
	@Column(name = "member_acc_price")
	@NotNull(message = "累積消費金額: 請勿空白")
	private Integer memberAccPrice;
	
	
	@Column(name = "member_discount")
	@NotNull(message = "折扣優惠: 請勿空白")
	@DecimalMax(value = "1.00", message = "折扣優惠: 打折數不能超過{value}，也就是頂多不打折，不可讓優惠價格高於原價格")
	private BigDecimal memberDiscount;
	
	
//	關聯
//	會員
	@OneToMany(mappedBy = "memberClass", cascade = CascadeType.ALL)
	@OrderBy("memberId asc")
	private Set<MemberVO> member;
	
	
	
	public MemberClassVO() {
		super();
	}
	
	public Integer getMemberClassId() {
		return memberClassId;
	}

	public void setMemberClassId(Integer memberClassId) {
		this.memberClassId = memberClassId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Integer getMemberAccPrice() {
		return memberAccPrice;
	}

	public void setMemberAccPrice(Integer memberAccPrice) {
		this.memberAccPrice = memberAccPrice;
	}

	public BigDecimal getMemberDiscount() {
		return memberDiscount;
	}

	public void setMemberDiscount(BigDecimal memberDiscount) {
		this.memberDiscount = memberDiscount;
	}

	public Set<MemberVO> getMember() {
		return member;
	}

	public void setMember(Set<MemberVO> member) {
		this.member = member;
	}

	
}
