package com.showise.movietype.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import com.showise.eachmovietype.model.EachMovieTypeVO;
import com.showise.memberprefertype.model.MemberPreferTypeVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
@Entity
@Table (name = "movie_type")
public class MovieTypeVO implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "movie_type_id",nullable=false)
	private Integer movieTypeId;
	@Column(name = "type_name",nullable=false)
	private String typeName;
	@Override
	public int hashCode() {
		return Objects.hash(movieTypeId);
	}
	
	@OneToMany (mappedBy = "movieType")
	Set<EachMovieTypeVO> eachMovieTypes;
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MovieTypeVO other = (MovieTypeVO) obj;
		return Objects.equals(movieTypeId, other.movieTypeId);
	}
	
	@OneToMany(mappedBy = "movieType",cascade = CascadeType.ALL)
	private Set<MemberPreferTypeVO> memSet;
	public MovieTypeVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MovieTypeVO(Integer movieType, String typeName, String typeDescription) {
		super();
		this.movieTypeId = movieType;
		this.typeName = typeName;
		this.typeDescription = typeDescription;
	}
	@Override
	public String toString() {
		return "MovieTypeVO [movieType=" + movieTypeId + ", typeName=" + typeName + ", typeDescription=" + typeDescription
				+ "]";
	}
	@Column(name = "type_description",nullable=false)
	private String typeDescription;
	public Integer getMovieTypeId() {
		return movieTypeId;
	}
	public void setMovieTypeId(Integer movieType) {
		this.movieTypeId = movieType;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getTypeDescription() {
		return typeDescription;
	}
	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}
	
	

}
