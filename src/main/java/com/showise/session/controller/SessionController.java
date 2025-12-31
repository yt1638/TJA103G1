package com.showise.session.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.cinema.model.CinemaService;
import com.showise.movie.model.MovieService;
import com.showise.session.model.SessionService;
import com.showise.session.model.SessionVO;

@Controller
@RequestMapping("/session")
public class SessionController {
 @Autowired 
 CinemaService cinemaService;
 @Autowired
 MovieService movieService;
 @Autowired 
 SessionService sessionService;
 
    @GetMapping("/")
    public String managePage(Model model,@RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate ) {
     
     LocalDate today = LocalDate.now();
     List<LocalDate> dateList = new ArrayList();
     for(int i = 0;i < 7; i++ ) {
      dateList.add(today.plusDays(i));
     }
     LocalDate selectedDate = (searchDate == null)?LocalDate.now():searchDate;
     
     model.addAttribute("dateList",dateList);
     model.addAttribute("cinemaList",cinemaService.getAllCinema());
     model.addAttribute("selectedDate",selectedDate);
     model.addAttribute("movieList",movieService.listByDate(selectedDate));
     model.addAttribute("sessionList",sessionService.listByDate(selectedDate));
     
     return "back-end/session/manage";
    }
    
    @PostMapping("/add")
    public String addSession(@RequestParam(value = "searchDate")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate,@RequestParam(value = "movieId") Integer movieId,@RequestParam(value = "cinemaId") Integer cinemaId,@RequestParam(value ="startTimeStr") String startTimeStr,RedirectAttributes ra) {
     
     try {
      sessionService.addSession(searchDate, movieId, cinemaId, startTimeStr);
      ra.addFlashAttribute("message","場次新增成功");
     }catch(IllegalArgumentException i) {
      ra.addFlashAttribute("error","新增失敗：新增場次與已存在場次時間重疊");
     }catch(Exception e) {
    	 e.printStackTrace();
     }
     
     return "redirect:/session/?searchDate="+searchDate;
    }
    
    @PostMapping("/updateStatus")
    public String toggleStatus(@RequestParam(value = "sessionId") Integer sessionId,RedirectAttributes ra) {
     try {
     sessionService.toggleStatus(sessionId);
     }catch(IllegalArgumentException i) {
    	 ra.addFlashAttribute("error","狀態更新失敗：該場次已有訂單記錄");
     }
     SessionVO sessionVO = sessionService.getById(sessionId);
     LocalDate sessionDate = sessionVO.getStartTime().toLocalDateTime().toLocalDate();
     
     return "redirect:/session/?searchDate="+sessionDate;
    }
    
    @PostMapping("/updateTime")
    public String updateTime(@RequestParam(value = "sessionId") Integer sessionId,@RequestParam(value = "newStartTimeStr") String newStartTimeStr,RedirectAttributes ra) {
     try {
      sessionService.updateSessionTime(sessionId, newStartTimeStr);
      ra.addFlashAttribute("message","場次時間更新成功");
     }catch(IllegalArgumentException i) {
      ra.addFlashAttribute("error","更新失敗：更新後時間與已存在場次重疊");
     }catch(IllegalStateException i) {
      ra.addFlashAttribute("error","場次編輯失敗：該場次已有訂單記錄"); 
     }
     
     SessionVO sessionVO = sessionService.getById(sessionId);
     LocalDate sessionDate = sessionVO.getStartTime().toLocalDateTime().toLocalDate();
     
     return "redirect:/session/?searchDate="+sessionDate;
    }
    
}