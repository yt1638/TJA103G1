package com.showise.foodcategory.model;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.showise.food.model.FoodVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table (name = "food_category")
public class FoodCategoryVO implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "food_category_id",nullable = false)
	private Integer foodCategoryId;
	public FoodCategoryVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		return Objects.hash(foodCategoryId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FoodCategoryVO other = (FoodCategoryVO) obj;
		return Objects.equals(foodCategoryId, other.foodCategoryId);
	}

	public Integer getFoodCategoryId() {
		return foodCategoryId;
	}

	public FoodCategoryVO(Integer foodCategoryId, String categoryName, Set<FoodVO> set) {
		super();
		this.foodCategoryId = foodCategoryId;
		this.categoryName = categoryName;
		this.foods = set;
	}

	public void setFoodCategoryId(Integer foodCategoryId) {
		this.foodCategoryId = foodCategoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Set<FoodVO> getSet() {
		return foods;
	}

	public void setSet(Set<FoodVO> set) {
		this.foods = set;
	}

	@Column (name = "category_name",nullable = false)
	private String categoryName;
	
	@OneToMany(mappedBy = "foodCate",cascade = CascadeType.ALL)
	private Set<FoodVO> foods = new HashSet<FoodVO>();

}
