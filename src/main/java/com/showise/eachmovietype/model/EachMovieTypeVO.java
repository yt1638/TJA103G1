package com.showise.eachmovietype.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.showise.movie.model.MovieVO;
import com.showise.movietype.model.MovieTypeVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (name = "each_movie_type_set")
public class EachMovieTypeVO implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "each_movie_type_id",nullable=false)
	private Integer eachMovieTypeId;
	
	
	@ManyToOne
	@JoinColumn(name ="movie_type_id", referencedColumnName="movie_type_id" )
	private MovieTypeVO movieType;

	@ManyToOne 
	@JsonIgnore
	@JoinColumn (name="movie_id",referencedColumnName = "movie_id")
	private MovieVO movie;

	

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof EachMovieTypeVO)) return false;
	    EachMovieTypeVO that = (EachMovieTypeVO) o;
	    return Objects.equals(movie, that.movie) &&
	           Objects.equals(movieType, that.movieType);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(movie, movieType);
	}



	public Integer getEachMovieTypeId() {
		return eachMovieTypeId;
	}

	public void setEachMovieTypeId(Integer eachMovieTypeId) {
		this.eachMovieTypeId = eachMovieTypeId;
	}

	public MovieTypeVO getMovieType() {
		return movieType;
	}

	public void setMovieType(MovieTypeVO movieType) {
		this.movieType = movieType;
	}

	public MovieVO getMovie() {
		return movie;
	}

	public void setMovie(MovieVO movie) {
		this.movie = movie;
	}

	public EachMovieTypeVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	

	
	
	

}
