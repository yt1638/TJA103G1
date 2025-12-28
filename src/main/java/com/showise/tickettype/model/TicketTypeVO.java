package com.showise.tickettype.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.showise.orderticket.model.OrderTicketVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "ticket_type")
public class TicketTypeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id")
    private Integer ticketTypeId;

    @Column(name = "ticket_NAME", nullable = false)
    @NotNull(message = "名稱: 請勿空白")
    private String ticketName;

    @Column(name = "ticket_PRICE", nullable = false)
    @NotNull(message = "價格: 請勿空白")
    private Integer ticketPrice;

    @NotNull(message = "內容: 請勿空白")
    @Column(name = "ticket_DESCRIPTION", nullable = false)    
    private String ticketDescription;
    
    @NotNull(message = "圖片: 請勿空白")
    @Column(name = "ticket_IMAGES", nullable = false)    
    private byte[] ticketImages;
    
    @OneToMany(mappedBy = "ticketType", cascade = CascadeType.ALL)
    private Set<OrderTicketVO> orderTickets = new HashSet<>();

	public Integer getTicketTypeId() {
		return ticketTypeId;
	}

	public void setTicketTypeId(Integer ticketTypeId) {
		this.ticketTypeId = ticketTypeId;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

	public Integer getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(Integer ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public String getTicketDescription() {
		return ticketDescription;
	}

	public void setTicketDescription(String ticketDescription) {
		this.ticketDescription = ticketDescription;
	}

	public byte[] getTicketImages() {
		return ticketImages;
	}

	public void setTicketImages(byte[] ticketImages) {
		this.ticketImages = ticketImages;
	}

	public Set<OrderTicketVO> getOrderTickets() {
		return orderTickets;
	}

	public void setOrderTickets(Set<OrderTicketVO> orderTickets) {
		this.orderTickets = orderTickets;
	}
	
}