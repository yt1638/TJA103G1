package com.showise.ordermail.model;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.showise.order.model.OrderVO;
import com.showise.orderfood.model.OrderFoodVO;
import com.showise.orderticket.model.OrderTicketVO;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class OrderMailService {

    /* ===== 從application.properties讀取 ===== */
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /* ===== 建立Mail Session===== */
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /* =====付款成功通知信===== */
    public void sendPaymentSuccessMail(OrderVO order) {

        try {
            String to =order.getMember().getEmail();
//            String to = "nineten20220910@gmail.com";

            String subject = "【SHOWISE小琉球影城】付款成功通知｜訂單 #" + order.getOrderId();
            String orderLink = "http://showise.ddns.net/member/memberTicket";

            /* ===== 座位 ===== */
            StringBuilder seatSb = new StringBuilder();
            for (OrderTicketVO ot : order.getOrderTickets()) {
                seatSb.append(ot.getSeat().getRowNo())
                      .append(ot.getSeat().getColumnNo())
                      .append(" ");
            }
            String seatText = seatSb.length() == 0 ? "無" : seatSb.toString().trim();

            /* ===== 餐飲 ===== */
            StringBuilder foodSb = new StringBuilder();
            if (order.getOrderFoods() == null || order.getOrderFoods().isEmpty()) {
                foodSb.append("無");
            } else {
                for (OrderFoodVO of : order.getOrderFoods()) {
                    foodSb.append(of.getFood().getFoodName())
                          .append(" × ")
                          .append(of.getFoodQuantity())
                          .append("、");
                }
                foodSb.deleteCharAt(foodSb.length() - 1);
            }

            /* ===== 日期時間 ===== */
            String date = order.getSession().getStartTime()
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String time = order.getSession().getStartTime()
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));

            /* ===== 信件內容 ===== */
            String html = String.format(
                "<p>親愛的會員您好，</p>" +
                "<p>您的訂單已付款成功，以下為訂單資訊：</p>" +
                "<ul>" +
                "<li>訂單編號：%d</li>" +
                "<li>電影：%s</li>" +
                "<li>日期：%s</li>" +
                "<li>場次：%s</li>" +
                "<li>座位：%s</li>" +
                "<li>餐飲：%s</li>" +
                "<li>總金額：$%s</li>" +
                "</ul>" +
                "<p>請前往「我的票券」查看詳情：" +"<a href='%s'>點我前往我的票券</a></p>" +
                "<p>感謝您的訂購！</p>",
                order.getOrderId(),
                order.getSession().getMovie().getNameTw(),
                date,
                time,
                seatText,
                foodSb.toString(),
                order.getTotalPrice(),
                orderLink
            );

            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(html, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("付款成功通知信已寄出：" + to);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
