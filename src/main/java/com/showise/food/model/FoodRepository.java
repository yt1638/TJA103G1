package com.showise.food.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FoodRepository extends JpaRepository<FoodVO,Integer>{
	
	@Query(value = "select distinct f from FoodVO f " +
		       "join fetch f.foodCate fc " +
		       "where fc.foodCategoryId = ?1 order by f.foodId desc")
	List<FoodVO> listByCate(Integer foodCategoryId);
	@Query(value = "From FoodVO where foodStatus = ?1")
	List<FoodVO> listByStatus(Integer foodStatus);
	@Query(value = "From FoodVO where foodName like %?1%")
	List<FoodVO> listByName(String foodName );
	@Query("from FoodVO f order by f.foodId desc")
	List<FoodVO> listAll();

}
