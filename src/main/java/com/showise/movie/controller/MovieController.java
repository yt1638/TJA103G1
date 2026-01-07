package com.showise.movie.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.showise.eachmovietype.model.EachMovieTypeVO;
import com.showise.movie.model.MovieService;
import com.showise.movie.model.MovieVO;
import com.showise.movietype.model.MovieTypeService;
import com.showise.movietype.model.MovieTypeVO;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/movie")
public class MovieController {
	@Autowired
	MovieService movieSvc;
	@Autowired
	MovieTypeService mtSvc;
	
	@GetMapping("/")
	public String listAll(Model model) {
		model.addAttribute("movieList",movieSvc.findAllByOrderByMovieIdDesc());
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/listAll :: content");
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/getDetail")
	public String getDetail(@RequestParam(value = "movieId") Integer movieId,Model model) {
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("movieVO",movieSvc.getById(movieId));
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/getDetail :: content");
		return "back-end/layout/admin-layout";
		
	}
	
	@GetMapping("/addMovie")
	public String addMovie(Model model) {
		MovieVO movieVO = new MovieVO();
		model.addAttribute("movieVO",movieVO);
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/save :: content");
		return "back-end/layout/admin-layout";
	}
	
	@PostMapping("/insert")
	public String insert(@Valid MovieVO movieVO,BindingResult result,Model model,@RequestParam(value = "movieTypeIds",required = false) List<Integer> movieTypeIds,@RequestParam(value = "imageFile",required = false) MultipartFile image) throws IOException {
		
		
		if(result.hasErrors()) {
			model.addAttribute("typeList",mtSvc.listAll());
			model.addAttribute("pageTitle","電影資料管理");
			model.addAttribute("content","back-end/movie/save :: content");
			return "back-end/layout/admin-layout";
		}
		
		if(!image.isEmpty()) {
			movieVO.setImage(image.getBytes());
		}else {
			movieVO.setImage(null);
		}
		
		Set<EachMovieTypeVO> set = new HashSet<EachMovieTypeVO>();
		if(movieTypeIds!=null) {
			for(Integer movieTypeId : movieTypeIds) {
				EachMovieTypeVO emtVO = new EachMovieTypeVO();
				MovieTypeVO mtVO = mtSvc.getById(movieTypeId);
				emtVO.setMovie(movieVO);
				emtVO.setMovieType(mtVO);
				set.add(emtVO);
			}
		}
		
		movieVO.setEachMovieTypes(set);
		
		movieSvc.addMovie(movieVO);
		return "redirect:/movie/";
	}
	
	@GetMapping("/imageReader")
	public void readImage(@RequestParam("movieId") Integer movieId,HttpServletResponse res) throws IOException {
		res.setContentType("image/jpeg");
		ServletOutputStream out = res.getOutputStream();
		byte[]image = movieSvc.getById(movieId).getImage();
		if(image!=null) {
			out.write(image);
		}
	}
	
	@GetMapping("/getOne_For_Update")
	public String getOne_For_Update(@RequestParam("movieId") Integer movieId,Model model) {
		MovieVO movieVO = movieSvc.getById(movieId);
		model.addAttribute("movieVO",movieVO);
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/save :: content");
		return "back-end/layout/admin-layout";
	}
	
	@PostMapping("/update")
	public String updateMovie(@Valid MovieVO movieVO,BindingResult result,@RequestParam(value = "imageFile",required = false) MultipartFile image,@RequestParam(value = "movieTypeIds",required = false) List<Integer> movieTypeIds,Model model) throws IOException {
		if(result.hasErrors()) {
			model.addAttribute("typeList",mtSvc.listAll());
			model.addAttribute("pageTitle","電影資料管理");
			model.addAttribute("content","back-end/movie/save :: content");
			return "back-end/layout/admin-layout";
		}
		
		if(!image.isEmpty()) {
			movieVO.setImage(image.getBytes());
		}else {
			byte[] originalImage = movieSvc.getById(movieVO.getMovieId()).getImage();
			movieVO.setImage(originalImage);
		}
		
		Set<EachMovieTypeVO> emtSet = new HashSet();
		if(movieTypeIds!=null) {
			for(Integer movieTypeId : movieTypeIds) {
				EachMovieTypeVO emtVO = new EachMovieTypeVO();
				MovieTypeVO mtVO = mtSvc.getById(movieTypeId);
				emtVO.setMovie(movieVO);
				emtVO.setMovieType(mtVO);
				emtSet.add(emtVO);
			}
		}
		movieVO.setEachMovieTypes(emtSet);
		movieSvc.addMovie(movieVO);
		return "redirect:/movie/";
	}
	@GetMapping("/listByMovieName")
	public String listByMovieName(@RequestParam(value="nameTw") String nameTw,Model model) {
		List<MovieVO> list = movieSvc.listByMovieName(nameTw);
		model.addAttribute("movieList",list);
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/listAll :: content");
		
		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/listByStatus")
	public String listByStatus(@RequestParam(value = "status") Integer status,Model model) {
		List<MovieVO> list = movieSvc.listByStatus(status);
		model.addAttribute("movieList",list);
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/listAll :: content");

		return "back-end/layout/admin-layout";
	}
	
	@GetMapping("/listByType")
	public String listByType(@RequestParam(value = "movieTypeId") Integer movieTypeId,Model model) {
		List<MovieVO> list = movieSvc.listByType(movieTypeId);
		model.addAttribute("movieList",list);
		model.addAttribute("typeList",mtSvc.listAll());
		model.addAttribute("pageTitle","電影資料管理");
		model.addAttribute("content","back-end/movie/listAll :: content");
		
		return "back-end/layout/admin-layout";
	}
	
	
	
	
	
	
	
	

}
