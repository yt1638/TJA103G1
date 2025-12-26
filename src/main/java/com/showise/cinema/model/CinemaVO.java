package com.showise.cinema.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.showise.seat.model.SeatVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name="cinema")
public class CinemaVO implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "cinema_id",nullable = false)
	private Integer cinemaId;
	
	@Column (name = "cinema_name",nullable = false)
	private String cinemaName;
	
	@Column (name = "cinema_total_seat",nullable = false)
	private Integer cinemaTotalSeat;
	
	@OneToMany (mappedBy = "cinema",cascade = CascadeType.ALL)	
	private Set<SeatVO> set;

	public Integer getCinemaId() {
		return cinemaId;
	}

	public void setCinemaId(Integer cinemaId) {
		this.cinemaId = cinemaId;
	}

	public String getCinemaName() {
		return cinemaName;
	}

	public void setCinemaName(String cinemaName) {
		this.cinemaName = cinemaName;
	}

	public Integer getCinemaTotalSeat() {
		return cinemaTotalSeat;
	}

	public void setCinemaTotalSeat(Integer cinemaTotalSeat) {
		this.cinemaTotalSeat = cinemaTotalSeat;
	}

	public Set<SeatVO> getSet() {
		return set;
	}

	public void setSet(Set<SeatVO> set) {
		this.set = set;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CinemaVO(Integer cinemaId, String cinemaName, Integer cinemaTotalSeat) {
		super();
		this.cinemaId = cinemaId;
		this.cinemaName = cinemaName;
		this.cinemaTotalSeat = cinemaTotalSeat;
	}

	public CinemaVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		return Objects.hash(cinemaId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CinemaVO other = (CinemaVO) obj;
		return Objects.equals(cinemaId, other.cinemaId);
	}
	
	

}
