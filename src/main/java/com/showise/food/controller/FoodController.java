package com.showise.food.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.showise.food.model.FoodService;
import com.showise.food.model.FoodVO;
import com.showise.foodcategory.model.FoodCateService;
import com.showise.foodcategory.model.FoodCategoryVO;

import jakarta.validation.Valid;

@Controller 
@RequestMapping("/food")
public class FoodController {
	
	@Autowired
	FoodService foodService;
	
	@Autowired
	FoodCateService foodCateService;
	
	@GetMapping("/")
	public String getAll(Model model) {
		model.addAttribute("foodList",foodService.getAll());
		return "back-end/food/listAll";
	}
	
	@GetMapping("/getDetail")
	public String getDetail(@RequestParam(value = "foodId") Integer foodId,Model model) {
		FoodVO foodVO = foodService.getById(foodId);
		model.addAttribute("foodVO",foodVO);
		return "/back-end/food.getDetail";
		
	}
	
	@PostMapping("/addFood")
	public String addFood(@Valid FoodVO foodVO,Model model,BindingResult result,@RequestParam(value = "foodCategoryId") Integer foodCategoryId,@RequestParam(value = "imageFile") MultipartFile image) throws IOException {
		
		if(result.hasErrors()) {
			return "back-end/food/liatAll";
		}
		
		if(!image.isEmpty()) {
			foodVO.setFoodImage(image.getBytes());
		}
		
		FoodCategoryVO fcVO = foodCateService.getById(foodCategoryId);
		foodVO.setFoodCate(fcVO);
		
		foodService.addFood(foodVO);
		
		return "redirect:/food/";
	}
	
	@PostMapping("/updateFood")
	public String updateFood(@Valid FoodVO foodVO,Model model,BindingResult result,@RequestParam(value = "foodCategoryId") Integer foodCategoryId,@RequestParam(value = "imageFile") MultipartFile image) throws IOException {
		
		if(image.isEmpty()) {
			byte[] oldImage = foodService.getById(foodVO.getFoodId()).getFoodImage();
			foodVO.setFoodImage(oldImage);
		}else {
			foodVO.setFoodImage(image.getBytes());
		}
		
		FoodCategoryVO fcVO = foodCateService.getById(foodCategoryId);
		foodVO.setFoodCate(fcVO);
		
		foodService.updateFood(foodVO);
		
		return "redirect:/food/";	
	}
	
	@GetMapping("/listByName")
	public String listByName(@RequestParam(value = "foodName") String foodName,Model model) {
		List<FoodVO> list = foodService.listByName(foodName);
		model.addAttribute("foodList",list);
		return "back-end/food/listAll";
	}
	
	@GetMapping("/listByStatus")
	public String listByStatus(Model model,@RequestParam(value = "status") Integer status) {
		List<FoodVO> list = foodService.listByStatus(status);
		model.addAttribute("foodList",list);
		return "back-end/food/listAll";
	}
	
	@GetMapping("/listByCate")
	public String listByType(Model model, @RequestParam (value = "foodCategoryId") Integer foodCategoryId) {
		List<FoodVO> list = foodService.listByCate(foodCategoryId);
		model.addAttribute("foodList",list);
		return "back-end/food/listAll";
	}
	
	
	
	

}
