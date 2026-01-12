package com.showise.payment.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.stream.Collectors;

public class EcpayCheckMac {

    public static String gen(Map<String, String> params, String hashKey, String hashIv) {
        // 1.將傳遞參數依照第一個英文字母，由A到Z的順序來排序，並且以&方式將所有參數串連。
        String sorted = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // 2.參數最前面加上HashKey、最後面加上HashIV
        String raw = "HashKey=" + hashKey + "&" + sorted + "&HashIV=" + hashIv;

        // 3.將整串字串進行URL encode，再轉為小寫
        String encoded = urlEncode(raw).toLowerCase();

        // 4.以SHA256加密方式來產生雜凑值，再轉大寫產生CheckMacValue
        return sha256(encoded).toUpperCase();
    }

    private static String urlEncode(String s) {
        try {
            String encoded = URLEncoder.encode(s, StandardCharsets.UTF_8.name());
            //urlEncode轉換規則
            return encoded
                    .replace("%2d", "-")
                    .replace("%5f", "_")
                    .replace("%2e", ".")
                    .replace("%21", "!")
                    .replace("%2a", "*")
                    .replace("%28", "(")
                    .replace("%29", ")");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

