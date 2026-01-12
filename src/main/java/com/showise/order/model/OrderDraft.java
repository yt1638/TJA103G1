package com.showise.order.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDraft implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer memberId;
	private Integer movieId;
	private String movieName;// 給前台顯示電影名稱使用
	private LocalDate date;
	private Integer sessionId;
	private String sessionTimeText; // 給前台顯示唱次按鈕使用

	// 電影票、餐飲、選取座位的清單
	private List<TicketItem> tickets = new ArrayList<>();
	private List<FoodItem> foods = new ArrayList<>();
	private List<SeatSelected> selectedSeats = new ArrayList<>();

	// 電影票、餐飲的金額與張數
	private Integer totalTicketQty;
	private BigDecimal ticketTotal;
	private BigDecimal foodTotal;
	private BigDecimal total;

	private String lockToken;
	private Long expireAt;

	//getter和setter
	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getMovieId() {
		return movieId;
	}

	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionTimeText() {
		return sessionTimeText;
	}

	public void setSessionTimeText(String sessionTimeText) {
		this.sessionTimeText = sessionTimeText;
	}

	public List<TicketItem> getTickets() {
		return tickets;
	}

	public void setTickets(List<TicketItem> tickets) {
		this.tickets = tickets;
	}

	public List<FoodItem> getFoods() {
		return foods;
	}

	public void setFoods(List<FoodItem> foods) {
		this.foods = foods;
	}

	public List<SeatSelected> getSelectedSeats() {
		return selectedSeats;
	}

	public void setSelectedSeats(List<SeatSelected> selectedSeats) {
		this.selectedSeats = selectedSeats;
	}

	public Integer getTotalTicketQty() {
		return totalTicketQty;
	}

	public void setTotalTicketQty(Integer totalTicketQty) {
		this.totalTicketQty = totalTicketQty;
	}

	public BigDecimal getTicketTotal() {
		return ticketTotal;
	}

	public void setTicketTotal(BigDecimal ticketTotal) {
		this.ticketTotal = ticketTotal;
	}

	public BigDecimal getFoodTotal() {
		return foodTotal;
	}

	public void setFoodTotal(BigDecimal foodTotal) {
		this.foodTotal = foodTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getLockToken() {
		return lockToken;
	}

	public void setLockToken(String lockToken) {
		this.lockToken = lockToken;
	}

	public Long getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Long expireAt) {
		this.expireAt = expireAt;
	}
	
	//TicketItem
	public static class TicketItem implements java.io.Serializable {
		private Integer ticketTypeId;
		private String name;
		private BigDecimal price;
		private Integer qty;
		private BigDecimal subtotal;
		
		//getter和setter
		public Integer getTicketTypeId() {
			return ticketTypeId;
		}
		public void setTicketTypeId(Integer ticketTypeId) {
			this.ticketTypeId = ticketTypeId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public Integer getQty() {
			return qty;
		}
		public void setQty(Integer qty) {
			this.qty = qty;
		}
		public BigDecimal getSubtotal() {
			return subtotal;
		}
		public void setSubtotal(BigDecimal subtotal) {
			this.subtotal = subtotal;
		}
		
	}
	
	//FoodItem
	public static class FoodItem implements java.io.Serializable {
		private Integer foodId;
		private String name;
		private BigDecimal price;
		private Integer qty;
		private BigDecimal subtotal;
		
		//getter和setter
		public Integer getFoodId() {
			return foodId;
		}
		public void setFoodId(Integer foodId) {
			this.foodId = foodId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public Integer getQty() {
			return qty;
		}
		public void setQty(Integer qty) {
			this.qty = qty;
		}
		public BigDecimal getSubtotal() {
			return subtotal;
		}
		public void setSubtotal(BigDecimal subtotal) {
			this.subtotal = subtotal;
		}
	}
	
	//SeatSelected
	public static class SeatSelected implements java.io.Serializable {
		private Integer seatId;
		private String row;
		private Integer seat;
		
		//getter和setter
		public Integer getSeatId() {
			return seatId;
		}
		public void setSeatId(Integer seatId) {
			this.seatId = seatId;
		}
		public String getRow() {
			return row;
		}
		public void setRow(String row) {
			this.row = row;
		}
		public Integer getSeat() {
			return seat;
		}
		public void setSeat(Integer seat) {
			this.seat = seat;
		}
	}
}
