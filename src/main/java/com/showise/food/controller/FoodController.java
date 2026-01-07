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
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
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
		model.addAttribute("foodCateList",foodCateService.getAll());
		model.addAttribute("pageTitle","餐飲管理");
		model.addAttribute("content","back-end/food/listAll :: content");
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/getDetail")
	public String getDetail(@RequestParam(value = "foodId") Integer foodId,Model model) {
		FoodVO foodVO = foodService.getById(foodId);
		model.addAttribute("foodVO",foodVO);
		return "/back-end/food.getDetail";
		
	}
	
	@GetMapping("/addFood")
	public String addFood(Model model) {
		FoodVO foodVO = new FoodVO();
		model.addAttribute("foodVO",foodVO);
		model.addAttribute("foodCateList",foodCateService.getAll());
		model.addAttribute("pageTitle","餐飲管理");
		model.addAttribute("content","back-end/food/save :: content");
		return "back-end/layout/admin-layout";
	}
	
	@PostMapping("/insert")
	public String addFood(@Valid FoodVO foodVO,BindingResult result,Model model,@RequestParam(value = "foodCategoryId") Integer foodCategoryId,@RequestParam(value = "imageFile") MultipartFile image) throws IOException {
		
		if(result.hasErrors()) {
			model.addAttribute("foodCateList",foodCateService.getAll());
			model.addAttribute("pageTitle","餐飲管理");
			model.addAttribute("content","back-end/food/save :: content");
			return "back-end/layout/admin-layout";
		}
		
		if(!image.isEmpty()) {
			foodVO.setFoodImage(image.getBytes());
		}
		
		FoodCategoryVO fcVO = foodCateService.getById(foodCategoryId);
		foodVO.setFoodCate(fcVO);
		
		foodService.addFood(foodVO);
		
		return "redirect:/food/";
	}
	
	@GetMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam(value = "foodId") Integer foodId,Model model) {
		model.addAttribute("foodVO",foodService.getById(foodId));
		model.addAttribute("foodCateList",foodCateService.getAll());
		model.addAttribute("pageTitle","餐飲管理");
		model.addAttribute("content","back-end/food/save :: content");
		return "back-end/layout/admin-layout";
	}
	
	@PostMapping("/update")
	public String updateFood(@Valid FoodVO foodVO,BindingResult result,Model model,@RequestParam(value = "foodCategoryId") Integer foodCategoryId,@RequestParam(value = "imageFile") MultipartFile image) throws IOException {
		
		if(result.hasErrors()) {
			model.addAttribute("foodCateList",foodCateService.getAll());
			model.addAttribute("pageTitle","餐飲管理");
			model.addAttribute("content","back-end/food/save :: content");
			return "back-end/layout/admin-layout";
		} 
		
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
		model.addAttribute("foodCateList",foodCateService.getAll());
		model.addAttribute("pageTitle","餐飲管理");
		model.addAttribute("content","back-end/food/listAll :: content");
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/listByStatus")
	public String listByStatus(Model model,@RequestParam(value = "status") Integer status) {
		List<FoodVO> list = foodService.listByStatus(status);
		model.addAttribute("foodList",list);
		model.addAttribute("foodCateList",foodCateService.getAll());
		model.addAttribute("pageTitle","餐飲管理");
		model.addAttribute("content","back-end/food/listAll :: content");
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/listByCate")
	public String listByType(Model model, @RequestParam (value = "foodCategoryId") Integer foodCategoryId) {
		List<FoodVO> list = foodService.listByCate(foodCategoryId);
		model.addAttribute("foodCateList",foodCateService.getAll());
		model.addAttribute("foodList",list);
		model.addAttribute("pageTitle","餐飲管理");
		model.addAttribute("content","back-end/food/listAll :: content");
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/imageReader")
	public void readImage(@RequestParam("foodId") Integer foodId,HttpServletResponse res) throws IOException {
		res.setContentType("image/jpeg");
		ServletOutputStream out = res.getOutputStream();
		byte[]image = foodService.getById(foodId).getFoodImage();
		if(image!=null) {
			out.write(image);
		}
	}
	
	
	
	

}
