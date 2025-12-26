package com.showise.orderticket.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.showise.order.model.OrderVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="order_ticket")
public class OrderTicketVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="order_ticket_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderTicketId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	private OrderVO order;
	
	@Column(name="seat_id")
//	@ManyToOne(fetch = FetchType.LAZY, optional = false)
//	@JoinColumn(name = "seat_id", referencedColumnName = "seat_id")
//	private SeatVO seat;
	private Integer seatId;
	
	@Column(name="ticket_type_id")
//	@ManyToOne(fetch = FetchType.LAZY, optional = false)
//	@JoinColumn(name = "ticket_type_id", referencedColumnName = "ticket_type_id")
//	private TicketTypeVO ticketType;
	private Integer ticketTypeId;
	
	@Column(name="ticket_price")
	private BigDecimal ticketPrice;

	public Integer getOrderTicketId() {
		return orderTicketId;
	}

	public void setOrderTicketId(Integer orderTicketId) {
		this.orderTicketId = orderTicketId;
	}

	public OrderVO getOrder() {
		return order;
	}

	public void setOrder(OrderVO order) {
		this.order = order;
	}

	public Integer getSeatId() {
		return seatId;
	}

	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}

	public Integer getTicketTypeId() {
		return ticketTypeId;
	}

	public void setTicketTypeId(Integer ticketTypeId) {
		this.ticketTypeId = ticketTypeId;
	}

	public BigDecimal getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(BigDecimal ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	


}
