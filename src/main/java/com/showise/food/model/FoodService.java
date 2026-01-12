package com.showise.food.model;

import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodService {
	
	@Autowired
	FoodRepository repository;
	
	@Transactional
	public void addFood(FoodVO foodVO) {
		repository.save(foodVO);
	}
	@Transactional
	public void updateFood(FoodVO foodVO) {
		repository.save(foodVO);
	}
	
	public FoodVO getById(Integer foodId) {
		Optional<FoodVO> optional = repository.findById(foodId);
		return optional.orElse(null);
	}
	
	public List<FoodVO> getAll(){
		return repository.listAll();
	}
	
	public List<FoodVO> listByStatus(Integer status){
		return repository.listByStatus(status);
	}
	
	public List<FoodVO> listByName(String foodName){
		return repository.listByName(foodName);
	}
	
	public List<FoodVO> listByCate(Integer foodCategoryId){
		return repository.listByCate(foodCategoryId);
	}

}
