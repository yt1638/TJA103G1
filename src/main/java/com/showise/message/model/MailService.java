package com.showise.message.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.member.model.MemberVO;
import com.showise.movie.model.MovieVO;
import com.showise.notification.preference.model.NotificationPreferenceService;
import com.showise.notification.preference.model.NotificationPreferenceVO;
import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;
import com.showise.order.model.OrderRepository;
import com.showise.order.model.OrderVO;
import com.showise.session.model.SessionRepository;

@Service
public class MailService {

	@Autowired
    private JavaMailSender mailSender;
	
	@Autowired
	OrderRepository ordRepository;
	
	@Autowired
	SessionRepository sessRepository;
	
	@Autowired
	NotificationPreferenceService npService;
	
	@Autowired
	NotificationShowstartService nsService;
	
	private void sendMail(String to,String subject,String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("a1587230@gmail.com");
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);
		
		mailSender.send(message);
	}
	
	private String buildSessionRemindContent(MessageVO message,OrderVO order) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		
		Timestamp ts = order.getSession().getStartTime();
		String sessionTime = ts.toLocalDateTime().format(format);
		
		return message.getMsgContent().replace("${movieName}", order.getSession().getMovie().getNameTw())
				.replace("${sessionTime}", sessionTime)
				.replace("${name}", order.getMember().getName())
				.replace("${orderId}", String.valueOf(order.getOrderId()));		
	}
	
	private String buildPreferRemindContent(MessageVO message,MovieVO movie,MemberVO member) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		
		return message.getMsgContent().replace("${movieName}", movie.getNameTw())
				.replace("${releaseDate}", String.valueOf(movie.getReleaseDate().format(format)))
				.replace("${name}",member.getName());
					
	}
	
	@Transactional
	public void sendSessionRemindMail(MessageVO message,OrderVO order) {
		if(Boolean.TRUE.equals(order.isSented())) {
			return;
		}
		
		boolean success = false;
		
		String subject = message.getMsgSubject();
		String content = buildSessionRemindContent(message,order);
		try {
		sendMail(order.getMember().getEmail(),subject,content);
		success = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(success) {
		order.setSented(true);
		}
		NotificationShowstartVO nsVO = new NotificationShowstartVO();
		nsVO.setMember(order.getMember());
		nsVO.setSession(order.getSession());
		nsVO.setNotiShowstScon(content);
		nsVO.setNotiShowstStat(success?1:0);
		nsVO.setNotiShowstStime(LocalDateTime.now());
		nsService.addNotificationShowstart(nsVO);
	}
	
	@Transactional 
	public void sendPreferRemindMail(MessageVO message,MovieVO movie,MemberVO member) {
		
		if(Boolean.TRUE.equals(movie.isSented())) {
			return;
		}
		
		boolean success = false;
		
		String subject = message.getMsgSubject();
		String content = buildPreferRemindContent(message, movie, member);
		try {
		sendMail(member.getEmail(),subject,content);
		success = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		NotificationPreferenceVO npVO = new NotificationPreferenceVO();
		npVO.setMember(member);
		npVO.setMovie(movie);
		npVO.setNotiPrefScon(content);
		npVO.setNotiPrefStat(success?1:0);
		npVO.setNotiPrefStime(LocalDateTime.now());
		npService.addNotificationPreference(npVO);	
	}
	
	@Transactional 
	public void setPreferSentStatus(MovieVO movie) {
		movie.setSented(true);
	}
	
	

}
