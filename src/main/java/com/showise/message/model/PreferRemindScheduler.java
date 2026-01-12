package com.showise.message.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.showise.eachmovietype.model.EachMovieTypeVO;
import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.movie.model.MovieService;
import com.showise.movie.model.MovieVO;
import com.showise.notification.preference.model.NotificationPreferenceVO;

import jakarta.transaction.Transactional;
@Service
public class PreferRemindScheduler {
	
	@Autowired
	MovieService movieService;
	
	@Autowired
	MessageService msgService;
	
	@Autowired 
	MemberService memService;
	
	@Autowired 
	MessageRepository msgRepository;
	
	@Autowired
	MailService mailService;
	
	@Transactional
	@Scheduled(cron = "0 26 20 * * ?", zone = "Asia/Taipei")
	public void sendPreferRemind() {
		
		int preHour = msgService.findByType(1).getPreHours();
        int preDays = preHour / 24;
		
        LocalDate today = LocalDate.now();
        LocalDate releaseDate = today.plusDays(preDays);
		
		List<MovieVO> movieList = movieService.findMovieToRemind(releaseDate);
		
		MessageVO template = msgService.findByType(1);
		
		
		for(MovieVO movie : movieList) {
			Set<MemberVO> memberSet = new HashSet<>();
		  for(EachMovieTypeVO eachType : movie.getEachMovieTypes()) {
			  Integer typeId = eachType.getMovieType().getMovieTypeId();
			  List<MemberVO> memberList = memService.findMemberByPrefer(typeId);
			  memberSet.addAll(memberList);
		  }
			
			for(MemberVO member : memberSet) {
				mailService.sendPreferRemindMail(template, movie, member);
			}
			mailService.setPreferSentStatus(movie);
		}
	
	}

}
