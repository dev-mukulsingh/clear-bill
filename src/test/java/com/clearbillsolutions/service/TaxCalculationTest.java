package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.BusinessSettingsDTO;
import com.clearbillsolutions.dto.CustomerDTO;
import com.clearbillsolutions.dto.InvoiceDTO;
import com.clearbillsolutions.dto.InvoiceItemDTO;
import com.clearbillsolutions.repository.CustomerRepository;
import com.clearbillsolutions.repository.InvoiceRepository;
import com.clearbillsolutions.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@DisplayName("GST Tax and Invoice Calculations Test")
class TaxCalculationTest {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BusinessSettingsService settingsService;

    @Test
    @DisplayName("Should correctly calculate intra-state GST (CGST 50% + SGST 50%)")
    void testIntraStateGstCalculation() {
        // Business state is Karnataka (set in dev data.sql)
        // Create a customer in Karnataka
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Intra State Client")
                .email("intra@karnataka.in")
                .state("Karnataka")
                .gstin("29AABCS1429B1ZB")
                .build());

        InvoiceItemDTO item1 = InvoiceItemDTO.builder()
                .description("Handcrafted Sweets")
                .hsnCode("210690")
                .quantity(2)
                .unitPrice(new BigDecimal("1000.00")) // 2000.00
                .taxRate(new BigDecimal("18.00"))      // 360.00
                .build();

        InvoiceDTO invoiceDTO = InvoiceDTO.builder()
                .customerId(customer.getId())
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(10))
                .discountRate(new BigDecimal("10.00")) // 10% of 2000 = 200.00
                .templateName("red")
                .items(List.of(item1))
                .build();

        InvoiceDTO created = invoiceService.createInvoice(invoiceDTO);

        assertNotNull(created);
        assertFalse(created.getIsInterState(), "Transaction within Karnataka must be intra-state");

        // Subtotal: 2 * 1000 = 2000.00
        assertEquals(new BigDecimal("2000.00"), created.getSubtotal());

        // Discount: 10% of 2000 = 200.00
        assertEquals(new BigDecimal("200.00"), created.getDiscountAmount());

        // Total Tax: 18% of 2000 = 360.00
        assertEquals(new BigDecimal("360.00"), created.getTaxAmount());

        // CGST & SGST half each: 180.00 and 180.00
        assertEquals(new BigDecimal("180.00"), created.getCgstAmount());
        assertEquals(new BigDecimal("180.00"), created.getSgstAmount());
        assertEquals(new BigDecimal("0.00"), created.getIgstAmount());

        // Grand Total: 2000.00 - 200.00 + 360.00 = 2160.00
        assertEquals(new BigDecimal("2160.00"), created.getTotalAmount());

        // Amount in words
        assertEquals("Rupees Two Thousand One Hundred Sixty Only", created.getAmountInWords());
    }

    @Test
    @DisplayName("Should correctly calculate inter-state GST (IGST 100%)")
    void testInterStateGstCalculation() {
        // Business state is Karnataka
        // Customer in Maharashtra
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Inter State Client")
                .email("inter@maharashtra.in")
                .state("Maharashtra")
                .gstin("27AABCP5678Q1Z2")
                .build());

        InvoiceItemDTO item1 = InvoiceItemDTO.builder()
                .description("Consultation Service")
                .hsnCode("998314")
                .quantity(1)
                .unitPrice(new BigDecimal("5000.00"))
                .taxRate(new BigDecimal("12.00")) // 600.00
                .build();

        InvoiceDTO invoiceDTO = InvoiceDTO.builder()
                .customerId(customer.getId())
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(10))
                .discountRate(BigDecimal.ZERO)
                .templateName("blue")
                .items(List.of(item1))
                .build();

        InvoiceDTO created = invoiceService.createInvoice(invoiceDTO);

        assertNotNull(created);
        assertTrue(created.getIsInterState(), "Transaction from Karnataka to Maharashtra must be inter-state");

        assertEquals(new BigDecimal("5000.00"), created.getSubtotal());
        assertEquals(new BigDecimal("0.00"), created.getDiscountAmount());
        assertEquals(new BigDecimal("600.00"), created.getTaxAmount());

        // CGST & SGST must be 0, IGST must be 600.00
        assertEquals(new BigDecimal("0.00"), created.getCgstAmount());
        assertEquals(new BigDecimal("0.00"), created.getSgstAmount());
        assertEquals(new BigDecimal("600.00"), created.getIgstAmount());

        // Grand Total: 5000.00 + 600.00 = 5600.00
        assertEquals(new BigDecimal("5600.00"), created.getTotalAmount());
        assertEquals("Rupees Five Thousand Six Hundred Only", created.getAmountInWords());
    }

    @Test
    @DisplayName("Should handle odd paise tax split without precision loss")
    void testOddPaiseTaxSplit() {
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Paise Precision Client")
                .email("paise@karnataka.in")
                .state("Karnataka")
                .build());

        // Unit price with tax that produces odd paise
        InvoiceItemDTO item1 = InvoiceItemDTO.builder()
                .description("Item with odd tax")
                .quantity(1)
                .unitPrice(new BigDecimal("35.00"))
                .taxRate(new BigDecimal("5.00")) // 35 * 0.05 = 1.75
                .build();

        InvoiceDTO created = invoiceService.createInvoice(InvoiceDTO.builder()
                .customerId(customer.getId())
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .items(List.of(item1))
                .build());

        assertEquals(new BigDecimal("1.75"), created.getTaxAmount());
        // 1.75 split into CGST (0.88) and SGST (0.87) or vice versa, sum equals 1.75
        BigDecimal sumSplit = created.getCgstAmount().add(created.getSgstAmount());
        assertEquals(new BigDecimal("1.75"), sumSplit, "CGST + SGST must exactly equal total tax without losing a single paisa");
    }

    @Test
    @DisplayName("Should handle zero percent tax rate (exempt supply)")
    void testZeroTaxRate() {
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Exempt Client")
                .email("exempt@karnataka.in")
                .state("Karnataka")
                .build());

        InvoiceItemDTO item1 = InvoiceItemDTO.builder()
                .description("Exempt Farm Fresh Product")
                .quantity(5)
                .unitPrice(new BigDecimal("200.00"))
                .taxRate(BigDecimal.ZERO)
                .build();

        InvoiceDTO created = invoiceService.createInvoice(InvoiceDTO.builder()
                .customerId(customer.getId())
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .items(List.of(item1))
                .build());

        assertEquals(new BigDecimal("1000.00"), created.getSubtotal());
        assertEquals(new BigDecimal("0.00"), created.getTaxAmount());
        assertEquals(new BigDecimal("0.00"), created.getCgstAmount());
        assertEquals(new BigDecimal("0.00"), created.getSgstAmount());
        assertEquals(new BigDecimal("1000.00"), created.getTotalAmount());
    }

    @Test
    @DisplayName("Should correctly handle multi-item invoice with mixed GST slabs (5%, 12%, 18%, 28%)")
    void testMixedGstSlabsInvoice() {
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Mixed Slabs Client")
                .email("mixed@client.in")
                .state("Karnataka")
                .build());

        InvoiceItemDTO item1 = InvoiceItemDTO.builder()
                .description("Item 5% GST")
                .quantity(10)
                .unitPrice(new BigDecimal("100.00")) // 1000.00 -> 50.00 tax
                .taxRate(new BigDecimal("5.00"))
                .build();

        InvoiceItemDTO item2 = InvoiceItemDTO.builder()
                .description("Item 12% GST")
                .quantity(2)
                .unitPrice(new BigDecimal("500.00")) // 1000.00 -> 120.00 tax
                .taxRate(new BigDecimal("12.00"))
                .build();

        InvoiceItemDTO item3 = InvoiceItemDTO.builder()
                .description("Item 18% GST")
                .quantity(1)
                .unitPrice(new BigDecimal("2000.00")) // 2000.00 -> 360.00 tax
                .taxRate(new BigDecimal("18.00"))
                .build();

        InvoiceItemDTO item4 = InvoiceItemDTO.builder()
                .description("Item 28% GST")
                .quantity(1)
                .unitPrice(new BigDecimal("1000.00")) // 1000.00 -> 280.00 tax
                .taxRate(new BigDecimal("28.00"))
                .build();

        InvoiceDTO created = invoiceService.createInvoice(InvoiceDTO.builder()
                .customerId(customer.getId())
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(10))
                .discountRate(new BigDecimal("5.00")) // 5% of 5000 = 250.00
                .items(List.of(item1, item2, item3, item4))
                .build());

        // Subtotal = 1000 + 1000 + 2000 + 1000 = 5000.00
        assertEquals(new BigDecimal("5000.00"), created.getSubtotal());
        // Discount 5% = 250.00
        assertEquals(new BigDecimal("250.00"), created.getDiscountAmount());
        // Tax = 50 + 120 + 360 + 280 = 810.00
        assertEquals(new BigDecimal("810.00"), created.getTaxAmount());
        // CGST = 405.00, SGST = 405.00
        assertEquals(new BigDecimal("405.00"), created.getCgstAmount());
        assertEquals(new BigDecimal("405.00"), created.getSgstAmount());
        // Total = 5000 - 250 + 810 = 5560.00
        assertEquals(new BigDecimal("5560.00"), created.getTotalAmount());
        assertEquals("Rupees Five Thousand Five Hundred Sixty Only", created.getAmountInWords());
    }

    @Test
    @DisplayName("Should handle very large invoice amounts (Crores) with precision")
    void testLargeCroreInvoice() {
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Enterprise Corp")
                .email("enterprise@corp.in")
                .state("Maharashtra") // Inter-state (IGST)
                .build());

        InvoiceItemDTO item = InvoiceItemDTO.builder()
                .description("Enterprise Cloud Infrastructure & Software Licenses")
                .quantity(1)
                .unitPrice(new BigDecimal("12500000.00")) // 1 Crore 25 Lakhs
                .taxRate(new BigDecimal("18.00")) // 18% of 1.25 Cr = 22,50,000.00
                .build();

        InvoiceDTO created = invoiceService.createInvoice(InvoiceDTO.builder()
                .customerId(customer.getId())
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .discountRate(BigDecimal.ZERO)
                .items(List.of(item))
                .build());

        assertEquals(new BigDecimal("12500000.00"), created.getSubtotal());
        assertEquals(new BigDecimal("22500000.00").divide(new BigDecimal("10"), 2, java.math.RoundingMode.HALF_UP), created.getTaxAmount());
        assertEquals(new BigDecimal("2250000.00"), created.getIgstAmount());
        assertEquals(new BigDecimal("14750000.00"), created.getTotalAmount());
        assertEquals("Rupees One Crore Forty-Seven Lakh Fifty Thousand Only", created.getAmountInWords());
    }

    @Test
    @DisplayName("Should omit UPI QR code when UPI ID is blank or null")
    void testOmitUpiQrWhenBlank() {
        BusinessSettingsDTO currentSettings = settingsService.getSettings();
        String originalUpi = currentSettings.getUpiId();
        try {
            currentSettings.setUpiId("");
            settingsService.updateSettings(currentSettings);

            CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                    .name("No UPI Client")
                    .email("noupi@client.in")
                    .state("Karnataka")
                    .build());

            InvoiceItemDTO item = InvoiceItemDTO.builder()
                    .description("Sample Item")
                    .quantity(1)
                    .unitPrice(new BigDecimal("100.00"))
                    .taxRate(new BigDecimal("18.00"))
                    .build();

            InvoiceDTO created = invoiceService.createInvoice(InvoiceDTO.builder()
                    .customerId(customer.getId())
                    .items(List.of(item))
                    .build());

            assertNull(created.getUpiPaymentUrl());
            assertNull(created.getUpiQrCodeBase64());
        } finally {
            currentSettings.setUpiId(originalUpi != null ? originalUpi : "clearbill@upi");
            settingsService.updateSettings(currentSettings);
        }
    }

    @Test
    @DisplayName("Should accept custom invoice number set by user")
    void testCustomInvoiceNumberCreation() {
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Custom Number Client")
                .email("custom@client.in")
                .state("Karnataka")
                .build());

        InvoiceItemDTO item = InvoiceItemDTO.builder()
                .description("Custom Item")
                .quantity(1)
                .unitPrice(new BigDecimal("250.00"))
                .taxRate(new BigDecimal("18.00"))
                .build();

        InvoiceDTO created = invoiceService.createInvoice(InvoiceDTO.builder()
                .invoiceNumber("CUSTOM-INV-999")
                .customerId(customer.getId())
                .items(List.of(item))
                .build());

        assertNotNull(created);
        assertEquals("CUSTOM-INV-999", created.getInvoiceNumber());
    }

    @Test
    @DisplayName("Should sanitize 'undefined', blank or null invoice number and generate sequential number")
    void testUndefinedOrNullInvoiceNumberFallsBackToSequence() {
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Auto Number Client")
                .email("auto@client.in")
                .state("Karnataka")
                .build());

        InvoiceItemDTO item = InvoiceItemDTO.builder()
                .description("Auto Item")
                .quantity(1)
                .unitPrice(new BigDecimal("100.00"))
                .taxRate(new BigDecimal("5.00"))
                .build();

        // 1. Literal "undefined" string
        InvoiceDTO createdUndefined = invoiceService.createInvoice(InvoiceDTO.builder()
                .invoiceNumber("undefined")
                .customerId(customer.getId())
                .items(List.of(item))
                .build());

        assertNotNull(createdUndefined.getInvoiceNumber());
        assertNotEquals("undefined", createdUndefined.getInvoiceNumber());
        assertTrue(createdUndefined.getInvoiceNumber().startsWith("INV-"));

        // 2. Blank string
        InvoiceDTO createdBlank = invoiceService.createInvoice(InvoiceDTO.builder()
                .invoiceNumber("   ")
                .customerId(customer.getId())
                .items(List.of(item))
                .build());

        assertNotNull(createdBlank.getInvoiceNumber());
        assertTrue(createdBlank.getInvoiceNumber().startsWith("INV-"));
    }

    @Test
    @DisplayName("Should reject duplicate custom invoice number with BadRequestException")
    void testDuplicateCustomInvoiceNumberThrowsBadRequest() {
        CustomerDTO customer = customerService.createCustomer(CustomerDTO.builder()
                .name("Duplicate Number Client")
                .email("duplicate@client.in")
                .state("Karnataka")
                .build());

        InvoiceItemDTO item = InvoiceItemDTO.builder()
                .description("Item")
                .quantity(1)
                .unitPrice(new BigDecimal("100.00"))
                .taxRate(new BigDecimal("5.00"))
                .build();

        invoiceService.createInvoice(InvoiceDTO.builder()
                .invoiceNumber("UNIQUE-NUM-001")
                .customerId(customer.getId())
                .items(List.of(item))
                .build());

        assertThrows(com.clearbillsolutions.exception.BadRequestException.class, () -> {
            invoiceService.createInvoice(InvoiceDTO.builder()
                    .invoiceNumber("UNIQUE-NUM-001")
                    .customerId(customer.getId())
                    .items(List.of(item))
                    .build());
        });
    }
}
