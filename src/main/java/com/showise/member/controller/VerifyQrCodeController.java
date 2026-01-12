package com.showise.member.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.showise.order.model.OrderService;
import com.showise.order.model.OrderVO;

@RestController
public class VerifyQrCodeController {

    @Autowired
    private OrderService orderService;

    /**
     * QR Code 驗票 API
     * 直接回傳 JSON，方便手機或瀏覽器
     */
    @GetMapping("/ticket/verify")
    @Transactional
    public Map<String, Object> verifyTicket(@RequestParam("code") String code) {
        Map<String, Object> result = new HashMap<>();

        // 處理空 code
        if (code == null || code.isEmpty()) {
            result.put("success", false);
            result.put("message", "驗票失敗：QR Code 無效");
            return result;
        }

        // 找訂單
        OrderVO order = orderService.findOrderByQrCode(code);
        if (order == null) {
            result.put("success", false);
            result.put("message", "驗票失敗：找不到訂單");
            return result;
        }

        // 已使用過
        if (Boolean.TRUE.equals(order.getUsed())) {
            result.put("success", false);
            result.put("message", "此票券已使用過，請勿重複驗票");
            return result;
        }

        // 標記為已驗票
        order.setUsed(true);
        orderService.updateOrder(order);

        // 回傳成功訊息
        result.put("success", true);
        result.put("orderId", order.getOrderId());
        result.put("movie", order.getSession().getMovie().getNameTw());
        result.put("message", "驗票成功！");

        return result;
    }

    // 生成 QR Code URL
    public static String generateQrUrl(String qrCode) {
        return "http://showise.ddns.net/ticket/verify?code=" +
               URLEncoder.encode(qrCode, StandardCharsets.UTF_8);
    }
}
