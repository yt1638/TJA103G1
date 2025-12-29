package com.showise.foodcategory.model;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodCateService {
	
	@Autowired
	FoodCateRepository repository;
	
	public FoodCategoryVO getById(Integer foodCategoryId) {
		Optional<FoodCategoryVO> optional = repository.findById(foodCategoryId);
		return optional.orElse(null);
	}
	
	public List<FoodCategoryVO> getAll(){
		return repository.findAll();
	}

}
