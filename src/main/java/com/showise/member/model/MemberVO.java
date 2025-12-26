package com.showise.member.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import com.showise.memberclass.model.MemberClassVO;
import com.showise.memberprefertype.model.MemberPreferTypeVO;
import com.showise.notification.preference.model.NotificationPreferenceVO;
import com.showise.notification.showstart.model.NotificationShowstartVO;
import com.showise.order.model.OrderVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "member")
public class MemberVO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", updatable = false)
	private Integer memberId;
	
	
//	會員等級編號
	@ManyToOne
	@JoinColumn(name = "member_class_id", referencedColumnName = "member_class_id")
	private MemberClassVO memberClass;
	
	
	@Column(name = "email")
	@NotEmpty(message = "信箱: 請勿空白")
	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "信箱: 只能是英文字母、數字和+_.-所組合 , 且須包含@")
	private String email;
	
	
	@Column(name = "password")
	@NotEmpty(message = "密碼: 請勿空白")
	@Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "密碼: 只能是英文字母、數字和_ , 且長度必需在6到20之間")
	private String password;
	
	
	@Column(name = "name")
	@NotEmpty(message = "會員姓名: 請勿空白")
	@Pattern(regexp = "^[(\\u4e00-\\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "姓名: 只能是中文、英文字母、數字和_ , 且長度必需在2到10之間")
	private String name;
	
	
	@Column(name = "phone")
	@NotEmpty(message = "電話: 請勿空白")
	@Pattern(regexp = "^09[0-9]{8}$", message = "電話: 須為09開頭，後面再加上8個數字")
	private String phone;
	
	
	@Column(name = "birthdate")
	@NotNull(message = "請選擇日期")
	@Past(message = "日期必須是今日(含)以前")
	private Date birthdate;
	
	
	@Column(name = "status")
	private Integer status;
	
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	
	@Column(name = "acc_consumption")
	private Integer accConsumption;
	
	
//	關聯
//	會員喜好電影類型
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@OrderBy("memberPreferTypeId asc")
	private Set<MemberPreferTypeVO> memberPreferType;

	
//	訂單
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@OrderBy("orderId asc")	
	private Set<OrderVO> order;

	
//	喜好通知
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@OrderBy("notiPrefNo asc")		
	private Set<NotificationPreferenceVO> notiPref;

	
// 	開演前通知
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@OrderBy("notiShowstNo asc")	
	private Set<NotificationShowstartVO> notiShowst;
	
	
	public MemberVO() {
		super();
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public MemberClassVO getMemberClass() {
		return memberClass;
	}

	public void setMemberClass(MemberClassVO memberClass) {
		this.memberClass = memberClass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	
	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Integer getAccConsumption() {
		return accConsumption;
	}

	public void setAccConsumption(Integer accConsumption) {
		this.accConsumption = accConsumption;
	}

	public Set<MemberPreferTypeVO> getMemberPreferType() {
		return memberPreferType;
	}

	public void setMemberPreferType(Set<MemberPreferTypeVO> memberPreferType) {
		this.memberPreferType = memberPreferType;
	}

	
	public Set<NotificationPreferenceVO> getNotiPref() {
		return notiPref;
	}

	public void setNotiPref(Set<NotificationPreferenceVO> notiPref) {
		this.notiPref = notiPref;
	}

	public Set<NotificationShowstartVO> getNotiShowst() {
		return notiShowst;
	}

	public void setNotiShowst(Set<NotificationShowstartVO> notiShowst) {
		this.notiShowst = notiShowst;
	}

	public Set<OrderVO> getOrder() {
		return order;
	}

	public void setOrder(Set<OrderVO> order) {
		this.order = order;
	}
	
}


