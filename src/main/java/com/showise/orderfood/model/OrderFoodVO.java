package com.showise.orderfood.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.showise.food.model.FoodVO;
import com.showise.order.model.OrderVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="order_food")
public class OrderFoodVO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="order_food_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderFoodId;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable=false)
	private OrderVO order;
	

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "food_id", nullable=false)
	private FoodVO food;
	
	@Column(name="food_price", nullable=false)
	private BigDecimal foodPrice;
	
	@Column(name="food_quantity", nullable=false)
	private Integer foodQuantity;

	public Integer getOrderFoodId() {
		return orderFoodId;
	}

	public void setOrderFoodId(Integer orderFoodId) {
		this.orderFoodId = orderFoodId;
	}

	public OrderVO getOrder() {
		return order;
	}

	public void setOrder(OrderVO order) {
		this.order = order;
	}

	public FoodVO getFood() {
		return food;
	}

	public void setFood(FoodVO food) {
		this.food = food;
	}

	public BigDecimal getFoodPrice() {
		return foodPrice;
	}

	public void setFoodPrice(BigDecimal foodPrice) {
		this.foodPrice = foodPrice;
	}

	public Integer getFoodQuantity() {
		return foodQuantity;
	}

	public void setFoodQuantity(Integer foodQuantity) {
		this.foodQuantity = foodQuantity;
	}

}
