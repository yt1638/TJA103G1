package com.showise.member.controller;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

// 使用ZXing生成QR Code，再轉成Base64字串，方便直接在網頁上以img顯示
public class QrCodeGenerator {				// 參數表示: 要生成QR Code的內容(如:網址、文字) | QR Code寬度 | QR Code高度
    public static String generateQRCodeBase64(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            // 呼叫encode()來生成QR Code
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // ByteArrayOutputStream將圖片寫入記憶體
            
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            // 將BitMatrix轉成PNG圖片格式，寫入baos
            
            return Base64.getEncoder().encodeToString(baos.toByteArray());		
            // 將QR Code圖片轉成Base64字串，方便之後可以直接嵌入HTML中

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}

// QRCodeWriter: QR Code生成器
// BitMatrix: 生成 QR Code的位元矩陣
// MatrixToImageWriter: 將BitMatrix轉成圖片格式
// BarcodeFormat: 定義生成條碼的類型(本專案是用QR Code)
// EncodeHintType: 生成QR Code的參數，如編碼方式