package com.showise.movie.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.showise.eachmovietype.model.EachMovieTypeVO;
import com.showise.notification.preference.model.NotificationPreferenceVO;
import com.showise.notification.showstart.model.NotificationShowstartVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
@Entity
@Table(name = "movie")
public class MovieVO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="movie_id",nullable=false)
	private Integer movieId;
	
	@NotEmpty(message = "中文電影名稱請勿空白")
	@Pattern(regexp="^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\p{P}]+$",message = "中文電影名稱僅能包含中文/數字/符號/空格")
	@Column(name="name_tw",nullable=false)
    private String nameTw;
	
	@NotEmpty(message = "英文電影名稱請勿空白")
	@Pattern(regexp = "^[a-zA-Z0-9\\s\\p{P}]+$", message = "只能包含英文、數字、空格與符號")
	@Column(name="name_eng",nullable=false)  
    private String nameEng;
	
	@Column(name="rating")
    private String rating;
	@JsonIgnore
	@Column(name="image")  
    private byte[] image;
	
	@NotEmpty(message = "導演請勿空白")
	@Column(name="director",nullable=false)  
    private String director;
	
	@Column(name="writer")
    private String writer;
	
	@NotEmpty(message = "演員請勿空白")
    @Column(name="actor",nullable=false)  
    private String actor;
	
	@NotNull(message = "片長請勿空白")
	@Min(value=1,message="片長請輸入大於1的數字")
    @Column(name="duration",nullable=false)  
    private Integer duration;
	
    @Column(name="introduction")
    private String introduction;
    
    @Column(name="trailer")
    private String trailer;
    
    @Column(name = "sented")
    private boolean sented;
    
    public boolean isSented() {
		return sented;
	}
	public void setSented(boolean sented) {
		this.sented = sented;
	}

	@NotEmpty(message = "電影年份請勿空白")
    @Pattern(regexp = "^\\d+$",message = "電影年份請填西元年份")
    @Min(value = 2000,message = "電影年份請填西元2000-2100年")
    @Max(value = 2100,message = "電影年份請填西元2000-2100年")
    @Column(name="produce_year")
    private String produceYear;
    
    @Override
	public int hashCode() {
		return Objects.hash(movieId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MovieVO other = (MovieVO) obj;
		return Objects.equals(movieId, other.movieId);
	}

	@Column(name="produce_country")
    private String produceCountry;
    @Column(name="publisher")
    private String publisher;
    @Column(name="release_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Column(name="end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @Column(name="create_time",nullable=false,insertable = false,updatable=false)  
    private Timestamp createTime;
    @Column(name="status",nullable=false)
    private Integer status;
    
    
    @OneToMany(mappedBy = "movie",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<EachMovieTypeVO> eachMovieTypes;
    
    @OneToMany(mappedBy = "movie",cascade = CascadeType.ALL)
    private Set<NotificationPreferenceVO> notiPSet;
    
    public Set<EachMovieTypeVO> getEachMovieTypes() {
    	return eachMovieTypes;
    }
    
    public void setEachMovieTypes(Set<EachMovieTypeVO> eachMovieTypes) {
    	this.eachMovieTypes = eachMovieTypes;
    }
    
	public Integer getMovieId() {
		return movieId;
	}
	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}
	public String getNameTw() {
		return nameTw;
	}
	public void setNameTw(String nameTw) {
		this.nameTw = nameTw;
	}
	public String getNameEng() {
		return nameEng;
	}
	public void setNameEng(String nameEng) {
		this.nameEng = nameEng;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getTrailer() {
		return trailer;
	}
	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}
	public String getProduceYear() {
		return produceYear;
	}
	public void setProduceYear(String produceYear) {
		this.produceYear = produceYear;
	}
	public String getProduceCountry() {
		return produceCountry;
	}
	public void setProduceCountry(String produceCountry) {
		this.produceCountry = produceCountry;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public LocalDate getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public MovieVO(Integer movieId, String nameTw, String nameEng, String rating, byte[] image, String director,
			String writer, String actor, Integer duration, String introduction, String trailer, String produceYear,
			String produceCountry,String publisher, LocalDate releaseDate, LocalDate endDate,
			Timestamp createTime, Integer status) {
		super();
		this.movieId = movieId;
		this.nameTw = nameTw;
		this.nameEng = nameEng;
		this.rating = rating;
		this.image = image;
		this.director = director;
		this.writer = writer;
		this.actor = actor;
		this.duration = duration;
		this.introduction = introduction;
		this.trailer = trailer;
		this.produceYear = produceYear;
		this.produceCountry = produceCountry;
		this.publisher = publisher;
		this.releaseDate = releaseDate;
		this.endDate = endDate;
		this.createTime = createTime;
		this.status = status;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public MovieVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}

}

