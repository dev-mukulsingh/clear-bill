package com.clearbillsolutions.controller;

import com.clearbillsolutions.dto.BusinessSettingsDTO;
import com.clearbillsolutions.dto.CustomerDTO;
import com.clearbillsolutions.dto.InvoiceDTO;
import com.clearbillsolutions.dto.InvoiceItemDTO;
import com.clearbillsolutions.dto.TaxSummaryItemDTO;
import com.clearbillsolutions.entity.InvoiceStatus;
import com.clearbillsolutions.service.BusinessSettingsService;
import com.clearbillsolutions.service.CustomerService;
import com.clearbillsolutions.service.InvoiceService;
import com.clearbillsolutions.service.PdfService;
import com.clearbillsolutions.util.GstUtil;
import com.clearbillsolutions.util.IndianCurrencyUtil;
import com.clearbillsolutions.util.QrCodeUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final BusinessSettingsService settingsService;
    private final CustomerService customerService;
    private final PdfService pdfService;

    public InvoiceController(InvoiceService invoiceService,
                             BusinessSettingsService settingsService,
                             CustomerService customerService,
                             PdfService pdfService) {
        this.invoiceService = invoiceService;
        this.settingsService = settingsService;
        this.customerService = customerService;
        this.pdfService = pdfService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices(
            @RequestParam(value = "status", required = false) InvoiceStatus status) {
        if (status != null) {
            return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
        }
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/next-number")
    public ResponseEntity<Map<String, String>> getNextInvoiceNumber() {
        String nextNumber = invoiceService.generateNextInvoiceNumber();
        return ResponseEntity.ok(Map.of(
                "invoiceNumber", nextNumber,
                "nextInvoiceNumber", nextNumber
        ));
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody InvoiceDTO dto) {
        InvoiceDTO created = invoiceService.createInvoice(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Long id, @Valid @RequestBody InvoiceDTO dto) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam("status") InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Download invoice as PDF formatted according to the specified template.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable Long id,
            @RequestParam(value = "template", required = false) String template) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        BusinessSettingsDTO settings = settingsService.getSettings();

        String selectedTemplate = (template != null && !template.trim().isEmpty())
                ? template
                : invoice.getTemplateName();

        byte[] pdfBytes = pdfService.generatePdf(invoice, settings, selectedTemplate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "invoice-" + invoice.getInvoiceNumber() + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Preview saved invoice HTML directly in the browser / iframe.
     */
    @GetMapping(value = "/{id}/preview", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> previewSavedInvoice(
            @PathVariable Long id,
            @RequestParam(value = "template", required = false) String template) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        BusinessSettingsDTO settings = settingsService.getSettings();

        String selectedTemplate = (template != null && !template.trim().isEmpty())
                ? template
                : invoice.getTemplateName();

        String html = pdfService.renderInvoiceHtml(invoice, settings, selectedTemplate);
        return ResponseEntity.ok(html);
    }

    /**
     * Live preview endpoint: renders unsaved/editing invoice data immediately.
     */
    @PostMapping(value = "/preview", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> previewDraftInvoice(
            @RequestBody InvoiceDTO dto,
            @RequestParam(value = "template", required = false) String template) {
        BusinessSettingsDTO settings = settingsService.getSettings();

        // Enrich customer info if customerId is given
        if (dto.getCustomerId() != null) {
            try {
                CustomerDTO customer = customerService.getCustomerById(dto.getCustomerId());
                if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
                    dto.setCustomerName(customer.getName());
                }
                if (dto.getCustomerEmail() == null || dto.getCustomerEmail().trim().isEmpty()) {
                    dto.setCustomerEmail(customer.getEmail());
                }
                if (dto.getCustomerPhone() == null || dto.getCustomerPhone().trim().isEmpty()) {
                    dto.setCustomerPhone(customer.getPhone());
                }
                if (dto.getCustomerAddress() == null || dto.getCustomerAddress().trim().isEmpty()) {
                    dto.setCustomerAddress(customer.getAddress());
                }
                if (dto.getCustomerCity() == null || dto.getCustomerCity().trim().isEmpty()) {
                    dto.setCustomerCity(customer.getCity());
                }
                if (dto.getCustomerState() == null || dto.getCustomerState().trim().isEmpty()) {
                    dto.setCustomerState(customer.getState());
                }
                if (dto.getCustomerPostalCode() == null || dto.getCustomerPostalCode().trim().isEmpty()) {
                    dto.setCustomerPostalCode(customer.getPostalCode());
                }
                if (dto.getCustomerCountry() == null || dto.getCustomerCountry().trim().isEmpty()) {
                    dto.setCustomerCountry(customer.getCountry());
                }
                if (dto.getCustomerGstin() == null || dto.getCustomerGstin().trim().isEmpty()) {
                    dto.setCustomerGstin(customer.getGstin());
                }
            } catch (Exception ignored) {
            }
        }

        // Fill defaults if null
        if (dto.getInvoiceNumber() == null || dto.getInvoiceNumber().trim().isEmpty() || "undefined".equalsIgnoreCase(dto.getInvoiceNumber().trim())) {
            dto.setInvoiceNumber("INV-DRAFT");
        }
        if (dto.getIssueDate() == null) {
            dto.setIssueDate(LocalDate.now());
        }
        if (dto.getDueDate() == null) {
            dto.setDueDate(LocalDate.now().plusDays(14));
        }
        if (dto.getStatus() == null) {
            dto.setStatus(InvoiceStatus.DRAFT);
        }

        boolean isIntra = GstUtil.isIntraState(settings.getState(), dto.getCustomerState());
        dto.setIsInterState(!isIntra);

        // Recalculate line totals, tax, CGST/SGST/IGST
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (InvoiceItemDTO item : dto.getItems()) {
                int qty = item.getQuantity() != null ? Math.max(1, item.getQuantity()) : 1;
                BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal taxRate = item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO;
                BigDecimal itemTax = lineTotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                BigDecimal cgst = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                BigDecimal sgst = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                BigDecimal igst = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

                if (isIntra) {
                    cgst = itemTax.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                    sgst = itemTax.subtract(cgst);
                } else {
                    igst = itemTax;
                }

                item.setQuantity(qty);
                item.setUnitPrice(price);
                item.setTaxRate(taxRate);
                item.setTaxAmount(itemTax);
                item.setCgstAmount(cgst);
                item.setSgstAmount(sgst);
                item.setIgstAmount(igst);
                item.setLineTotal(lineTotal);

                subtotal = subtotal.add(lineTotal);
                totalCgst = totalCgst.add(cgst);
                totalSgst = totalSgst.add(sgst);
                totalIgst = totalIgst.add(igst);
            }
        }

        BigDecimal discountRate = dto.getDiscountRate() != null ? dto.getDiscountRate() : BigDecimal.ZERO;
        BigDecimal discountAmount = subtotal.multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalTax = totalCgst.add(totalSgst).add(totalIgst);
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(totalTax);

        dto.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        dto.setCgstAmount(totalCgst.setScale(2, RoundingMode.HALF_UP));
        dto.setSgstAmount(totalSgst.setScale(2, RoundingMode.HALF_UP));
        dto.setIgstAmount(totalIgst.setScale(2, RoundingMode.HALF_UP));
        dto.setTaxAmount(totalTax.setScale(2, RoundingMode.HALF_UP));
        dto.setDiscountRate(discountRate.setScale(2, RoundingMode.HALF_UP));
        dto.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        dto.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));

        // Tax summary
        if (dto.getItems() != null) {
            Map<BigDecimal, List<InvoiceItemDTO>> byRate = dto.getItems().stream()
                    .collect(Collectors.groupingBy(item -> item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO));

            List<TaxSummaryItemDTO> taxSummary = new ArrayList<>();
            byRate.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        BigDecimal rate = entry.getKey();
                        BigDecimal taxable = entry.getValue().stream().map(InvoiceItemDTO::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal cgst = entry.getValue().stream().map(InvoiceItemDTO::getCgstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal sgst = entry.getValue().stream().map(InvoiceItemDTO::getSgstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal igst = entry.getValue().stream().map(InvoiceItemDTO::getIgstAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal tax = cgst.add(sgst).add(igst);
                        BigDecimal halfRate = rate.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

                        taxSummary.add(TaxSummaryItemDTO.builder()
                                .gstRate(rate)
                                .taxableAmount(taxable)
                                .cgstRate(!isIntra ? BigDecimal.ZERO : halfRate)
                                .cgstAmount(cgst)
                                .sgstRate(!isIntra ? BigDecimal.ZERO : halfRate)
                                .sgstAmount(sgst)
                                .igstRate(!isIntra ? rate : BigDecimal.ZERO)
                                .igstAmount(igst)
                                .totalTax(tax)
                                .build());
                    });
            dto.setTaxSummary(taxSummary);
        }

        // Amount in words
        dto.setAmountInWords(IndianCurrencyUtil.toWords(totalAmount));

        // Formatted currency
        dto.setFormattedTotal(IndianCurrencyUtil.format(totalAmount));
        dto.setFormattedSubtotal(IndianCurrencyUtil.format(subtotal));
        dto.setFormattedTaxAmount(IndianCurrencyUtil.format(totalTax));
        dto.setFormattedDiscountAmount(IndianCurrencyUtil.format(discountAmount));

        // UPI QR Code
        if (settings.getUpiId() != null && !settings.getUpiId().trim().isEmpty()) {
            String payee = settings.getPayeeName() != null && !settings.getPayeeName().trim().isEmpty()
                    ? settings.getPayeeName()
                    : settings.getCompanyName();
            String upiUrl = QrCodeUtil.buildUpiUrl(settings.getUpiId(), payee, totalAmount, dto.getInvoiceNumber());
            dto.setUpiPaymentUrl(upiUrl);
            dto.setUpiQrCodeBase64(QrCodeUtil.generateQrBase64(upiUrl, 200, 200));
        }

        String selectedTemplate = (template != null && !template.trim().isEmpty())
                ? template
                : (dto.getTemplateName() != null ? dto.getTemplateName() : "red");

        String html = pdfService.renderInvoiceHtml(dto, settings, selectedTemplate);
        return ResponseEntity.ok(html);
    }
}
