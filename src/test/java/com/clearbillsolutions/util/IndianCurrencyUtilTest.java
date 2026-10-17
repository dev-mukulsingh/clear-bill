package com.clearbillsolutions.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IndianCurrencyUtil Tests")
class IndianCurrencyUtilTest {

    @Test
    @DisplayName("Should format zero properly")
    void testFormatZero() {
        assertEquals("₹0.00", IndianCurrencyUtil.format(BigDecimal.ZERO));
        assertEquals("₹0.00", IndianCurrencyUtil.format(new BigDecimal("0.00")));
        assertEquals("₹0.00", IndianCurrencyUtil.format(null));
    }

    @Test
    @DisplayName("Should format paise and small amounts")
    void testFormatPaise() {
        assertEquals("₹0.50", IndianCurrencyUtil.format(new BigDecimal("0.5")));
        assertEquals("₹0.05", IndianCurrencyUtil.format(new BigDecimal("0.05")));
        assertEquals("₹5.00", IndianCurrencyUtil.format(new BigDecimal("5")));
        assertEquals("₹99.99", IndianCurrencyUtil.format(new BigDecimal("99.99")));
    }

    @Test
    @DisplayName("Should format hundreds and thousands")
    void testFormatThousands() {
        assertEquals("₹500.00", IndianCurrencyUtil.format(new BigDecimal("500.00")));
        assertEquals("₹1,234.50", IndianCurrencyUtil.format(new BigDecimal("1234.50")));
        assertEquals("₹9,999.00", IndianCurrencyUtil.format(new BigDecimal("9999")));
    }

    @Test
    @DisplayName("Should format Indian lakh and crore grouping")
    void testFormatLakhsAndCrores() {
        // ₹1,25,000.00 as explicitly required
        assertEquals("₹1,25,000.00", IndianCurrencyUtil.format(new BigDecimal("125000.00")));
        // Ten lakhs
        assertEquals("₹10,00,000.00", IndianCurrencyUtil.format(new BigDecimal("1000000.00")));
        // One crore
        assertEquals("₹1,00,00,000.00", IndianCurrencyUtil.format(new BigDecimal("10000000.00")));
        // Ten crores
        assertEquals("₹10,00,00,000.00", IndianCurrencyUtil.format(new BigDecimal("100000000.00")));
        // Very large number with paise
        assertEquals("₹52,34,56,789.75", IndianCurrencyUtil.format(new BigDecimal("523456789.75")));
    }

    @Test
    @DisplayName("Should format negative amounts")
    void testFormatNegative() {
        assertEquals("-₹1,25,000.00", IndianCurrencyUtil.format(new BigDecimal("-125000.00")));
        assertEquals("-₹50.00", IndianCurrencyUtil.format(new BigDecimal("-50")));
    }

    @Test
    @DisplayName("Should convert zero to words")
    void testWordsZero() {
        assertEquals("Rupees Zero Only", IndianCurrencyUtil.toWords(BigDecimal.ZERO));
        assertEquals("Rupees Zero Only", IndianCurrencyUtil.toWords(null));
    }

    @Test
    @DisplayName("Should convert round rupee amounts to words")
    void testWordsRoundRupees() {
        assertEquals("Rupees Five Only", IndianCurrencyUtil.toWords(new BigDecimal("5.00")));
        assertEquals("Rupees Twenty-Five Only", IndianCurrencyUtil.toWords(new BigDecimal("25.00")));
        assertEquals("Rupees Three Hundred Forty-Five Only", IndianCurrencyUtil.toWords(new BigDecimal("345.00")));
        assertEquals("Rupees One Thousand Two Hundred Thirty-Four Only", IndianCurrencyUtil.toWords(new BigDecimal("1234.00")));
        assertEquals("Rupees One Lakh Twenty-Five Thousand Only", IndianCurrencyUtil.toWords(new BigDecimal("125000.00")));
        assertEquals("Rupees One Crore Only", IndianCurrencyUtil.toWords(new BigDecimal("10000000.00")));
    }

    @Test
    @DisplayName("Should convert amounts with paise to words")
    void testWordsWithPaise() {
        assertEquals("Rupees One Lakh Twenty-Five Thousand and Fifty Paise Only",
                IndianCurrencyUtil.toWords(new BigDecimal("125000.50")));
        assertEquals("Rupees Zero and Seventy-Five Paise Only",
                IndianCurrencyUtil.toWords(new BigDecimal("0.75")));
        assertEquals("Rupees Five and Fifty Paise Only",
                IndianCurrencyUtil.toWords(new BigDecimal("5.50")));
    }

    @Test
    @DisplayName("Should convert very large numbers into Crores and Lakhs")
    void testWordsLargeNumbers() {
        String result = IndianCurrencyUtil.toWords(new BigDecimal("523456789.75"));
        assertTrue(result.startsWith("Rupees Fifty-Two Crore Thirty-Four Lakh Fifty-Six Thousand Seven Hundred Eighty-Nine and Seventy-Five Paise Only"));
    }
}
