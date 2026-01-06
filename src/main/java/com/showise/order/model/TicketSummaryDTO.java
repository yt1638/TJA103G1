package com.showise.order.model;

import java.math.BigDecimal;

public class TicketSummaryDTO {
	private String ticketName;
	private BigDecimal price;
	private int quantity;
	
	public TicketSummaryDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TicketSummaryDTO(String ticketName, BigDecimal price) {
		super();
		this.ticketName = ticketName;
		this.price = price;
		this.quantity = 0;
	}
	//一種票券的數量
	public void addOne() {
		this.quantity++;
	}
	//一種票券的金額小計
	public BigDecimal getSubTotal() {
		return price.multiply(BigDecimal.valueOf(quantity));
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
