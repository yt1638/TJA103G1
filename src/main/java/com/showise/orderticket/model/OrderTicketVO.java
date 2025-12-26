package com.showise.orderticket.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.showise.order.model.OrderVO;
import com.showise.seat.model.SeatVO;
import com.showise.ticket.model.TicketTypeVO;

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
	

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "seat_id", referencedColumnName = "seat_id")
	private SeatVO seat;


	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ticket_type_id", referencedColumnName = "ticket_type_id")
	private TicketTypeVO ticketType;
	
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

	public SeatVO getSeat() {
		return seat;
	}

	public void setSeat(SeatVO seat) {
		this.seat = seat;
	}

	public TicketTypeVO getTicketType() {
		return ticketType;
	}

	public void setTicketType(TicketTypeVO ticketType) {
		this.ticketType = ticketType;
	}

	public BigDecimal getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(BigDecimal ticketPrice) {
		this.ticketPrice = ticketPrice;
	}
}
