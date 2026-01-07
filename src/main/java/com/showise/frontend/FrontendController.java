package com.showise.frontend;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.showise.food.model.FoodService;
import com.showise.movie.model.MovieService;
import com.showise.movie.model.MovieVO;
import com.showise.movietype.model.MovieTypeService;
import com.showise.session.model.SessionService;
import com.showise.session.model.SessionVO;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/front")
@Controller
public class FrontendController {
	
	@Autowired
	MovieService movieService;
	@Autowired
	MovieTypeService movieTypeService;
	@Autowired 
	FoodService foodService;
	@Autowired 
	SessionService sessionService;
	
	@GetMapping("/index")
	public String index(Model model) {
		
		model.addAttribute("ingMovie",movieService.listByStatus(1));
		model.addAttribute("comingMovie",movieService.listByStatus(2));
		model.addAttribute("typeList",movieTypeService.listAll());
		
		return "front-end/index";
		
	}
	
	@GetMapping("/filter")
	@ResponseBody
	public ResponseEntity<Map<String,List<MovieVO>>> filterMovie(@RequestParam(value = "movieTypeId") Integer movieTypeId){
		Map<String,List<MovieVO>> response = new HashMap<>(); 
		
		response.put("ingMovie",movieService.listByStatusAndType(1, movieTypeId) );
		response.put("comingMovie", movieService.listByStatusAndType(2, movieTypeId));
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/imageReader")
	public void readImage(@RequestParam(required = false) Integer movieId,@RequestParam(required = false) Integer foodId,HttpServletResponse res) throws IOException {
		if(movieId!=null) {
			res.setContentType("image/jpeg");
			ServletOutputStream out = res.getOutputStream();
			byte[] image = movieService.getById(movieId).getImage();
			if(image!=null) {
			out.write(image);
			}
		}
		if(foodId!=null) {
			res.setContentType("image/jpeg");
			ServletOutputStream out = res.getOutputStream();
			byte[] image = foodService.getById(foodId).getFoodImage();
			if(image!=null) {
			out.write(image);
			}
		}
	}
	
	
	@GetMapping("movieDetail")
	public String getDetail(Model model,@RequestParam("movieId") Integer movieId) {
		
		MovieVO movieVO = movieService.getById(movieId);
		
		List<LocalDate> datelist = new ArrayList();
		for(int i = 0;i<7;i++) {
			LocalDate today = LocalDate.now();
			datelist.add(today.plusDays(i));
		}
		
		model.addAttribute("movie",movieVO);
		model.addAttribute("datelist",datelist);
		model.addAttribute("session0",sessionService.listByMovieAndDate(movieId, datelist.get(0)));
		model.addAttribute("session1",sessionService.listByMovieAndDate(movieId, datelist.get(1)));
		model.addAttribute("session2",sessionService.listByMovieAndDate(movieId, datelist.get(2)));
		model.addAttribute("session3",sessionService.listByMovieAndDate(movieId, datelist.get(3)));
		model.addAttribute("session4",sessionService.listByMovieAndDate(movieId, datelist.get(4)));
		model.addAttribute("session5",sessionService.listByMovieAndDate(movieId, datelist.get(5)));
		model.addAttribute("session6",sessionService.listByMovieAndDate(movieId, datelist.get(6)));
		
		return "front-end/moviedetail";
	}
	
	@GetMapping("/getSessionStatus")
	@ResponseBody // 回傳 JSON
	public SessionVO getStatus(@RequestParam Integer sessionId) {
	    // 這裡 Jackson 會把 SessionVO 轉成 JSON
	    // 注意：之前提到的 @JsonIgnore 必須有加，否則這裡也會循環報錯
	    return sessionService.getById(sessionId);
	}
	
//	@GetMapping("/ticketStep1")
//	public String toTicketPage(@RequestParam("sessionId") Integer sessionId,@RequestParam("movieId") Integer movieId,@RequestParam("date") LocalDate date,HttpSession session,Model model) {
//		session.setAttribute("movieId", movieId);
//		session.setAttribute("sessionId", sessionId);
//		session.setAttribute("date", date);
//		
//		
//		
//		
//	}
	
	

}
