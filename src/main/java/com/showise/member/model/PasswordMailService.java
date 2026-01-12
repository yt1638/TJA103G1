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
public class PasswordMailService {
	
	@Value("${mail.gmail.username}")
	private String myGmail;
	
	@Value("${mail.gmail.password}")
	private String myGmailPassword;
	
	public void sendPwdMail(String to, String password) {
		
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
            message.setSubject("Showise電影院-密碼重設信件");
            
            message.setText(
            		"您好:\r\n"
            		+ "您申請了忘記密碼信件，若您沒有申請，請忽略此封信件。\r\n"
            		+ "您的密碼:"+ password + "\r\n" 
            		+ "\r\n"
            		+ "登入後，您可再次修改密碼。\r\n"
            		+ "貼心提醒: 此密碼 5 分鐘內有效，敬請把握時間完成驗證，謝謝您。"
    		);
            Transport.send(message);
            
		}catch(MessagingException e) {
			throw new RuntimeException("密碼重設信件寄送失敗", e);
		}
		
	}
	
}
