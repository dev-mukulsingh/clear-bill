package com.clearbillsolutions.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for generating UPI payment URLs and QR code images in Base64 PNG format.
 */
public class QrCodeUtil {

    private static final Logger log = LoggerFactory.getLogger(QrCodeUtil.class);

    /**
     * Builds a standard UPI payment deep link string:
     * upi://pay?pa=<upi-id>&pn=<name>&am=<amount>&cu=INR&tn=<invoice-number>
     */
    public static String buildUpiUrl(String upiId, String payeeName, BigDecimal amount, String invoiceNumber) {
        if (upiId == null || upiId.trim().isEmpty()) {
            return null;
        }

        try {
            String pa = upiId.trim();
            String pn = payeeName != null ? URLEncoder.encode(payeeName.trim(), StandardCharsets.UTF_8) : "";
            String am = amount != null ? amount.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
            String tn = invoiceNumber != null ? URLEncoder.encode(invoiceNumber.trim(), StandardCharsets.UTF_8) : "";

            return String.format("upi://pay?pa=%s&pn=%s&am=%s&cu=INR&tn=%s", pa, pn, am, tn);
        } catch (Exception e) {
            log.error("Failed to build UPI payment URL: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Generates a QR code image for the given content and returns a Base64 data URI string.
     */
    public static String generateQrBase64(String content, int width, int height) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            log.error("Failed to generate QR code image: {}", e.getMessage());
            return null;
        }
    }
}
