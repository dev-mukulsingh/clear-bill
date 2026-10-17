package com.clearbillsolutions.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GST & QR Utilities Test")
class GstAndQrUtilTest {

    @Test
    @DisplayName("Should validate GSTIN syntax")
    void testGstinValidation() {
        // Valid 15-char GSTINs
        assertTrue(GstUtil.isValidGstin("29AABCH1234F1Z8"));
        assertTrue(GstUtil.isValidGstin("27AABCP5678Q1Z2"));
        assertTrue(GstUtil.isValidGstin("07AAAAA0000A1Z5"));

        // Optional / blank is allowed
        assertTrue(GstUtil.isValidGstin(null));
        assertTrue(GstUtil.isValidGstin(""));
        assertTrue(GstUtil.isValidGstin("   "));

        // Invalid formats
        assertFalse(GstUtil.isValidGstin("12345")); // too short
        assertFalse(GstUtil.isValidGstin("29AABCH1234F1Z899")); // too long
        assertFalse(GstUtil.isValidGstin("INVALID_GSTIN_1"));
    }

    @Test
    @DisplayName("Should detect intra-state vs inter-state supply")
    void testIntraStateDetection() {
        assertTrue(GstUtil.isIntraState("Karnataka", "Karnataka"));
        assertTrue(GstUtil.isIntraState("karnataka", "KARNATAKA"));
        assertTrue(GstUtil.isIntraState(" Maharashtra ", "Maharashtra"));

        assertFalse(GstUtil.isIntraState("Karnataka", "Maharashtra"));
        assertFalse(GstUtil.isIntraState("Delhi", "Haryana"));

        // Null default
        assertTrue(GstUtil.isIntraState(null, "Karnataka"));
        assertTrue(GstUtil.isIntraState("Karnataka", null));
    }

    @Test
    @DisplayName("Should construct valid UPI payment URL")
    void testUpiUrlBuilding() {
        String url = QrCodeUtil.buildUpiUrl("clearbill@upi", "ClearBill Solutions", new BigDecimal("2984.00"), "INV-2026-0001");
        assertNotNull(url);
        assertTrue(url.startsWith("upi://pay?"));
        assertTrue(url.contains("pa=clearbill%40upi".replace("%40", "@")) || url.contains("pa=clearbill@upi"));
        assertTrue(url.contains("am=2984.00"));
        assertTrue(url.contains("cu=INR"));
        assertTrue(url.contains("tn=INV-2026-0001"));
    }

    @Test
    @DisplayName("Should generate valid Base64 PNG QR code")
    void testQrGeneration() {
        String qr = QrCodeUtil.generateQrBase64("upi://pay?pa=clearbill@upi&am=100.00", 200, 200);
        assertNotNull(qr);
        assertTrue(qr.startsWith("data:image/png;base64,"));
        assertTrue(qr.length() > 100);
    }

    @Test
    @DisplayName("Should return null UPI URL and QR when UPI ID is null or blank")
    void testBlankUpiHandling() {
        assertNull(QrCodeUtil.buildUpiUrl(null, "ClearBill Solutions", new BigDecimal("100.00"), "INV-1"));
        assertNull(QrCodeUtil.buildUpiUrl("", "ClearBill Solutions", new BigDecimal("100.00"), "INV-1"));
        assertNull(QrCodeUtil.buildUpiUrl("   ", "ClearBill Solutions", new BigDecimal("100.00"), "INV-1"));
        assertNull(QrCodeUtil.generateQrBase64(null, 200, 200));
        assertNull(QrCodeUtil.generateQrBase64("", 200, 200));
    }
}
