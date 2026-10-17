package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.BusinessSettingsDTO;
import com.clearbillsolutions.dto.InvoiceDTO;
import com.clearbillsolutions.dto.InvoiceItemDTO;
import com.clearbillsolutions.dto.TaxSummaryItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
public class PdfGenerationTest {

    @Autowired
    private PdfService pdfService;

    private InvoiceDTO createSampleInvoice(String template) {
        InvoiceItemDTO item = InvoiceItemDTO.builder()
                .id(1L)
                .description("Handmade Organic Saffron Pack (10g)")
                .hsnCode("091020")
                .quantity(2)
                .unitPrice(new BigDecimal("1500.00"))
                .gstRate(new BigDecimal("5.00"))
                .taxRate(new BigDecimal("5.00"))
                .lineTotal(new BigDecimal("3000.00"))
                .cgstAmount(new BigDecimal("75.00"))
                .sgstAmount(new BigDecimal("75.00"))
                .igstAmount(BigDecimal.ZERO)
                .build();

        TaxSummaryItemDTO tax = TaxSummaryItemDTO.builder()
                .gstRate(new BigDecimal("5.00"))
                .taxableAmount(new BigDecimal("3000.00"))
                .cgstRate(new BigDecimal("2.50"))
                .cgstAmount(new BigDecimal("75.00"))
                .sgstRate(new BigDecimal("2.50"))
                .sgstAmount(new BigDecimal("75.00"))
                .igstRate(BigDecimal.ZERO)
                .igstAmount(BigDecimal.ZERO)
                .totalTax(new BigDecimal("150.00"))
                .build();

        return InvoiceDTO.builder()
                .id(101L)
                .invoiceNumber("INV-2026-TEST")
                .customerId(1L)
                .customerName("Ananya Sharma")
                .customerAddress("Flat 402, Royal Residency, Indiranagar")
                .customerCity("Bengaluru")
                .customerState("Karnataka")
                .customerPostalCode("560038")
                .customerCountry("India")
                .customerGstin("29ABCDE1234F1Z5")
                .customerPhone("+91 98765 43210")
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .status(com.clearbillsolutions.entity.InvoiceStatus.PAID)
                .subtotal(new BigDecimal("3000.00"))
                .taxAmount(new BigDecimal("150.00"))
                .cgstAmount(new BigDecimal("75.00"))
                .sgstAmount(new BigDecimal("75.00"))
                .igstAmount(BigDecimal.ZERO)
                .isInterState(false)
                .totalAmount(new BigDecimal("3150.00"))
                .amountInWords("Rupees Three Thousand One Hundred Fifty Only")
                .templateName(template)
                .upiPaymentUrl("upi://pay?pa=clearbill@upi&pn=ClearBill&am=3150.00&cu=INR&tn=INV-2026-TEST")
                .items(List.of(item))
                .taxSummary(List.of(tax))
                .notes("Thank you for your business! Please settle promptly.")
                .build();
    }

    private BusinessSettingsDTO createSampleSettings() {
        return BusinessSettingsDTO.builder()
                .companyName("ClearBill Solutions")
                .email("billing@clearbillsolutions.com")
                .phone("+91 98765 00000")
                .address("Shop 10, MG Road")
                .city("Bengaluru")
                .state("Karnataka")
                .stateCode("29")
                .postalCode("560001")
                .gstin("29AABCH1234F1Z8")
                .upiId("clearbill@upi")
                .payeeName("ClearBill Solutions")
                .currencySymbol("₹")
                .build();
    }

    @Test
    @DisplayName("Generate PDF for Red template")
    void testRedTemplatePdf() {
        byte[] pdf = pdfService.generatePdf(createSampleInvoice("red"), createSampleSettings(), "red");
        assertNotNull(pdf);
        assertTrue(pdf.length > 5000, "Red template PDF should have valid size");
    }

    @Test
    @DisplayName("Generate PDF for Blue template")
    void testBlueTemplatePdf() {
        byte[] pdf = pdfService.generatePdf(createSampleInvoice("blue"), createSampleSettings(), "blue");
        assertNotNull(pdf);
        assertTrue(pdf.length > 5000, "Blue template PDF should have valid size");
    }

    @Test
    @DisplayName("Generate PDF for Green template")
    void testGreenTemplatePdf() {
        byte[] pdf = pdfService.generatePdf(createSampleInvoice("green"), createSampleSettings(), "green");
        assertNotNull(pdf);
        assertTrue(pdf.length > 5000, "Green template PDF should have valid size");
    }

    @Test
    @DisplayName("Generate PDF for Orange template")
    void testOrangeTemplatePdf() {
        byte[] pdf = pdfService.generatePdf(createSampleInvoice("orange"), createSampleSettings(), "orange");
        assertNotNull(pdf);
        assertTrue(pdf.length > 5000, "Orange template PDF should have valid size");
    }

    @Test
    @DisplayName("Generate PDF for Black template")
    void testBlackTemplatePdf() {
        byte[] pdf = pdfService.generatePdf(createSampleInvoice("black"), createSampleSettings(), "black");
        assertNotNull(pdf);
        assertTrue(pdf.length > 5000, "Black template PDF should have valid size");
    }

    @Test
    @DisplayName("Generate PDF fallback for unknown or legacy template")
    void testFallbackTemplatePdf() {
        byte[] pdf = pdfService.generatePdf(createSampleInvoice("unknown-legacy"), createSampleSettings(), "unknown-legacy");
        assertNotNull(pdf);
        assertTrue(pdf.length > 5000, "Fallback template PDF should default to Red and succeed");
    }
}
