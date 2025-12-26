package com.showise.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.showise.member.model.MemberVO;
import com.showise.orderfood.model.OrderFoodVO;
import com.showise.orderticket.model.OrderTicketVO;
import com.showise.session.model.SessionVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_info")
public class OrderVO implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionVO session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberVO member;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "order_status", nullable = false)
    private Integer orderStatus;

    @Column(name = "order_create_time", updatable = false, insertable=false)
    private Timestamp orderCreateTime;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<OrderFoodVO> orderFoods = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<OrderTicketVO> orderTickets = new HashSet<>();

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public SessionVO getSession() {
		return session;
	}

	public void setSession(SessionVO session) {
		this.session = session;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Timestamp getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(Timestamp orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public Set<OrderFoodVO> getOrderFoods() {
		return orderFoods;
	}

	public void setOrderFoods(Set<OrderFoodVO> orderFoods) {
		this.orderFoods = orderFoods;
	}

	public Set<OrderTicketVO> getOrderTickets() {
		return orderTickets;
	}

	public void setOrderTickets(Set<OrderTicketVO> orderTickets) {
		this.orderTickets = orderTickets;
	}

	
}
