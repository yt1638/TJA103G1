package com.showise.tickettype.model;

import java.io.Serializable;
import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.showise.member.model.MemberVO;
import com.showise.movie.model.MovieVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
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
    private Integer ticketPRICE;

    @NotNull(message = "內容: 請勿空白")
    @Column(name = "ticket_DESCRIPTION", nullable = false)    
    private String ticketDescription;
    
    @NotNull(message = "圖片: 請勿空白")
    @Column(name = "ticket_IMAGES", nullable = false)    
    private Short ticketImages;


    public TicketTypeVO getticketTypeId(TicketTypeVO ticketTypeId) {
        return ticketTypeId;

}

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

	public Integer getTicketPRICE() {
		return ticketPRICE;
	}

	public void setTicketPRICE(Integer ticketPRICE) {
		this.ticketPRICE = ticketPRICE;
	}

	public String getTicketDescription() {
		return ticketDescription;
	}

	public void setTicketDescription(String ticketDescription) {
		this.ticketDescription = ticketDescription;
	}

	public Short getTicketImages() {
		return ticketImages;
	}

	public void setTicketImages(Short ticketImages) {
		this.ticketImages = ticketImages;
	}

	
}