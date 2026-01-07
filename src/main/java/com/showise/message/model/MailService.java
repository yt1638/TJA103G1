package com.showise.message.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	@Autowired
    private JavaMailSender mailSender;

	@Value("${spring.mail.username:}")
	private String from;
     
    public void sendTextMail(String to, String subject, String content) {
        if (mailSender == null) {
            throw new IllegalStateException("JavaMailSender 尚未載入（缺少 spring-boot-starter-mail 依賴或 classpath 未更新）");
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        mailSender.send(msg);
    }

}
