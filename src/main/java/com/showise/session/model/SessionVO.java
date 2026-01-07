package com.showise.session.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.showise.cinema.model.CinemaVO;
import com.showise.movie.model.MovieVO;
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
import jakarta.persistence.Table;


@Entity
@Table (name = "session")
public class SessionVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Column (name = "session_id",nullable = false)
	private Integer sessionId;
	@Column (name = "start_time",nullable = false)
	private Timestamp startTime;
	@Column (name = "end_time",nullable = false)
	private Timestamp endTime;
	@Column (name = "all_seat_status",nullable = false)
	private String allSeatStatus;
	@Column(name = "session_status",nullable = false)
	private Integer sessionStatus;
	
	
	@ManyToOne
	@JoinColumn (name = "movie_id", referencedColumnName = "movie_id")
	private MovieVO movie;
	@ManyToOne
	@JsonIgnore
	@JoinColumn (name = "cinema_id" , referencedColumnName = "cinema_id")
	private CinemaVO cinema;
	@OneToMany (mappedBy = "session",cascade = CascadeType.ALL)
	private Set<OrderVO> orders;
	@OneToMany (mappedBy = "session",cascade = CascadeType.ALL)
	private Set<NotificationShowstartVO> notishows;
	
	
	
	public Integer getSessionStatus() {
		return sessionStatus;
	}
	public void setSessionStatus(Integer sessionStatus) {
		this.sessionStatus = sessionStatus;
	}
	public Set<OrderVO> getOrders() {
		return orders;
	}
	public void setOrders(Set<OrderVO> orders) {
		this.orders = orders;
	}
	public Set<NotificationShowstartVO> getNotishows() {
		return notishows;
	}
	public void setNotishows(Set<NotificationShowstartVO> notishows) {
		this.notishows = notishows;
	}
	public Integer getSessionId() {
		return sessionId;
	}
	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public String getAllSeatStatus() {
		return allSeatStatus;
	}
	public void setAllSeatStatus(String allSeatStatus) {
		this.allSeatStatus = allSeatStatus;
	}
	public MovieVO getMovie() {
		return movie;
	}
	public void setMovie(MovieVO movie) {
		this.movie = movie;
	}
	public CinemaVO getCinema() {
		return cinema;
	}
	public void setCinema(CinemaVO cinema) {
		this.cinema = cinema;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public SessionVO(Integer sessionId, Timestamp startTime, Timestamp endTime, String allSeatStatus) {
		super();
		this.sessionId = sessionId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.allSeatStatus = allSeatStatus;
	}
	@Override
	public int hashCode() {
		return Objects.hash(sessionId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionVO other = (SessionVO) obj;
		return Objects.equals(sessionId, other.sessionId);
	}
	public SessionVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	

}
