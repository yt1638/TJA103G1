package com.showise.seat.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.showise.cinema.model.CinemaVO;
import com.showise.orderticket.model.OrderTicketVO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table (name = "seat")
public class SeatVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column (name = "seat_id",nullable = false)
	private Integer seatId;
	@Column (name = "seat_row",nullable = false, length = 1, columnDefinition = "CHAR(1)")
	private String rowNo;
	@Column (name = "seat_column",nullable = false)
	private Integer columnNo;
	@Column (name = "seat_status",nullable = false)
	private Integer seatStatus;
	@OneToMany (mappedBy = "seat",cascade = CascadeType.ALL)
	private Set<OrderTicketVO> set;
	
	
	@ManyToOne 
	@JoinColumn (name = "cinema_id" , referencedColumnName="cinema_id")
	private CinemaVO cinema;


	public Integer getSeatId() {
		return seatId;
	}


	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}


	public String getRow() {
		return rowNo;
	}


	public void setRow(String rowNo) {
		this.rowNo = rowNo;
	}


	public Integer getColumn() {
		return columnNo;
	}


	public void setColumn(Integer columnNo) {
		this.columnNo = columnNo;
	}


	public Integer getSeatStatus() {
		return seatStatus;
	}


	public void setSeatStatus(Integer seatStatus) {
		this.seatStatus = seatStatus;
	}


	public CinemaVO getCinema() {
		return cinema;
	}


	public void setCinema(CinemaVO cinema) {
		this.cinema = cinema;
	}


	public SeatVO(Integer seatId, String rowNo, Integer columnNo, Integer seatStatus) {
		super();
		this.seatId = seatId;
		this.rowNo = rowNo;
		this.columnNo = columnNo;
		this.seatStatus = seatStatus;
	}


	public SeatVO() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public int hashCode() {
		return Objects.hash(seatId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeatVO other = (SeatVO) obj;
		return Objects.equals(seatId, other.seatId);
	}
	
	
	
	
	
	
	
	

}
