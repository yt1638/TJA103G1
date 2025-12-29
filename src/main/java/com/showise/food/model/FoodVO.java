package com.showise.food.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.showise.foodcategory.model.FoodCategoryVO;
import com.showise.orderfood.model.OrderFoodVO;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table (name="food")
public class FoodVO implements Serializable{
	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "food_id",nullable = false)
	private Integer foodId;
	
	
	@ManyToOne
	@JoinColumn(name ="food_category_id",referencedColumnName="food_category_id",nullable = false)
	private FoodCategoryVO foodCate;
	@OneToMany(mappedBy = "food",cascade = CascadeType.ALL)
	private Set<OrderFoodVO> orderFoods = new HashSet<OrderFoodVO>();
	
	
	@NotEmpty(message = "餐飲名稱請勿空白")
	@Column (name = "food_name",nullable = false)
	private String foodName;
	public Set<OrderFoodVO> getOrderFoods() {
		return orderFoods;
	}
	public void setOrderFoods(Set<OrderFoodVO> orderFoods) {
		this.orderFoods = orderFoods;
	}
	@Column (name = "food_image")
	private byte[] foodImage;
	public Integer getFoodId() {
		return foodId;
	}
	public FoodVO(Integer foodId, FoodCategoryVO foodCate, String foodName, byte[] foodImage, Integer foodOriginalPrice,
			Integer foodPrice, Integer foodStatus) {
		super();
		this.foodId = foodId;
		this.foodCate = foodCate;
		this.foodName = foodName;
		this.foodImage = foodImage;
		this.foodOriginalPrice = foodOriginalPrice;
		this.foodPrice = foodPrice;
		this.foodStatus = foodStatus;
	}
	@Override
	public int hashCode() {
		return Objects.hash(foodId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FoodVO other = (FoodVO) obj;
		return Objects.equals(foodId, other.foodId);
	}
	public FoodVO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setFoodId(Integer foodId) {
		this.foodId = foodId;
	}
	public FoodCategoryVO getFoodCate() {
		return foodCate;
	}
	public void setFoodCate(FoodCategoryVO foodCate) {
		this.foodCate = foodCate;
	}
	public String getFoodName() {
		return foodName;
	}
	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	public byte[] getFoodImage() {
		return foodImage;
	}
	public void setFoodImage(byte[] foodImage) {
		this.foodImage = foodImage;
	}
	public Integer getFoodOriginalPrice() {
		return foodOriginalPrice;
	}
	public void setFoodOriginalPrice(Integer foodOriginalPrice) {
		this.foodOriginalPrice = foodOriginalPrice;
	}
	public Integer getFoodPrice() {
		return foodPrice;
	}
	public void setFoodPrice(Integer foodPrice) {
		this.foodPrice = foodPrice;
	}
	public Integer getFoodStatus() {
		return foodStatus;
	}
	public void setFoodStatus(Integer foodStatus) {
		this.foodStatus = foodStatus;
	}
	@NotNull(message = "餐飲原價請勿空白")
	@Min(value=1,message="售價請填正整數")
	@Column (name = "food_original_price")
	private Integer foodOriginalPrice;
	@NotNull(message = "餐飲網路售價請勿空白")
	@Min(value=1,message="售價請填正整數")
	@Column (name = "food_price")
	private Integer foodPrice;
	@Column (name = "food_status")
	private Integer foodStatus;

}
