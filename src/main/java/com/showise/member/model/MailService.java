package com.showise.member.model;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	
	@Value("${mail.gmail.username}")
	private String myGmail;
	
	@Value("${mail.gmail.password}")
	private String myGmailPassword;
	
	public void sendAuthCodeMail(String to, String authCode) {
		
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            
            Session session = Session.getInstance(props,
            		new Authenticator() {
            			@Override
            			protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(myGmail, myGmailPassword);
                        }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(myGmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("Showise電影院-會員註冊驗證碼");
            
            message.setText(
            		"您好:\r\n"
            		+ "感謝您向Showise電影院申請註冊帳號，為了啟用相關服務並保障您的帳戶安全，敬請您協助進行帳號驗證。\r\n"
            		+ "\r\n"
            		+ "請您在影智推電影院帳號驗證的頁面，輸入驗證碼。\r\n"
            		+ "您的驗證碼:"+ authCode + "\r\n" 
            		+ "\r\n"
            		+ "註冊完成後。影智推電影院將提供您更多的服務資訊。\r\n"
            		+ "最後，再次由衷感謝您註冊影智推電影院帳號。\r\n"
            		+ "貼心提醒: 此驗證碼 5 分鐘內有效，敬請把握時間完成驗證，謝謝您。"
    		);
            Transport.send(message);
            
		}catch(MessagingException e) {
			throw new RuntimeException("會員驗證碼Email寄送失敗", e);
		}
		
	}
	
}
