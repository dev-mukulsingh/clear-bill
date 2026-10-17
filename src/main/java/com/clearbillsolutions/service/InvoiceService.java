package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.InvoiceDTO;
import com.clearbillsolutions.dto.InvoiceItemDTO;
import com.clearbillsolutions.dto.TaxSummaryItemDTO;
import com.clearbillsolutions.entity.BusinessSettings;
import com.clearbillsolutions.entity.Customer;
import com.clearbillsolutions.entity.Invoice;
import com.clearbillsolutions.entity.InvoiceItem;
import com.clearbillsolutions.entity.InvoiceStatus;
import com.clearbillsolutions.entity.Product;
import com.clearbillsolutions.exception.BadRequestException;
import com.clearbillsolutions.exception.ResourceNotFoundException;
import com.clearbillsolutions.repository.CustomerRepository;
import com.clearbillsolutions.repository.InvoiceRepository;
import com.clearbillsolutions.repository.ProductRepository;
import com.clearbillsolutions.util.GstUtil;
import com.clearbillsolutions.util.IndianCurrencyUtil;
import com.clearbillsolutions.util.QrCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Set<String> VALID_TEMPLATES = Set.of(
            "red", "blue", "green", "orange", "black"
    );

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BusinessSettingsService settingsService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          CustomerRepository customerRepository,
                          ProductRepository productRepository,
                          BusinessSettingsService settingsService) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.settingsService = settingsService;
    }

    public String sanitizeTemplateName(String templateName) {
        if (templateName == null || templateName.trim().isEmpty()) {
            return "red";
        }
        String clean = templateName.trim().toLowerCase().replace("_", "-");
        return VALID_TEMPLATES.contains(clean) ? clean : "red";
    }

    @Transactional(readOnly = true)
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return toDTO(invoice);
    }

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("Invoice must have at least one line item.");
        }

        boolean hasExplicitNumber = dto.getInvoiceNumber() != null
                && !dto.getInvoiceNumber().trim().isEmpty()
                && !"undefined".equalsIgnoreCase(dto.getInvoiceNumber().trim());

        String invoiceNumber = hasExplicitNumber
                ? dto.getInvoiceNumber().trim()
                : generateNextInvoiceNumber();

        if (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            if (hasExplicitNumber) {
                throw new BadRequestException("Invoice number already exists: " + invoiceNumber);
            }
            invoiceNumber = generateNextInvoiceNumber();
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setCustomer(customer);
        invoice.setIssueDate(dto.getIssueDate() != null ? dto.getIssueDate() : LocalDate.now());
        invoice.setDueDate(dto.getDueDate() != null ? dto.getDueDate() : LocalDate.now().plusDays(14));
        invoice.setStatus(dto.getStatus() != null ? dto.getStatus() : InvoiceStatus.DRAFT);
        invoice.setNotes(dto.getNotes());
        invoice.setTemplateName(sanitizeTemplateName(dto.getTemplateName()));

        BigDecimal discountRate = dto.getDiscountRate() != null ? dto.getDiscountRate() : BigDecimal.ZERO;
        invoice.setDiscountRate(discountRate);

        BusinessSettings settings = settingsService.getOrCreateSettings();
        populateAndCalculateItems(invoice, customer, settings, dto.getItems(), discountRate);

        Invoice saved = invoiceRepository.save(invoice);
        return toDTO(saved);
    }

    @Transactional
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO dto) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BadRequestException("Invoice must have at least one line item.");
        }

        if (dto.getInvoiceNumber() != null && !dto.getInvoiceNumber().trim().isEmpty() && !"undefined".equalsIgnoreCase(dto.getInvoiceNumber().trim())) {
            String newNumber = dto.getInvoiceNumber().trim();
            if (!newNumber.equalsIgnoreCase(invoice.getInvoiceNumber()) && invoiceRepository.existsByInvoiceNumber(newNumber)) {
                throw new BadRequestException("Invoice number already in use: " + newNumber);
            }
            invoice.setInvoiceNumber(newNumber);
        }

        invoice.setCustomer(customer);
        invoice.setIssueDate(dto.getIssueDate());
        invoice.setDueDate(dto.getDueDate());
        if (dto.getStatus() != null) {
            invoice.setStatus(dto.getStatus());
        }
        invoice.setNotes(dto.getNotes());
        if (dto.getTemplateName() != null && !dto.getTemplateName().trim().isEmpty()) {
            invoice.setTemplateName(sanitizeTemplateName(dto.getTemplateName()));
        }

        BigDecimal discountRate = dto.getDiscountRate() != null ? dto.getDiscountRate() : BigDecimal.ZERO;
        invoice.setDiscountRate(discountRate);

        BusinessSettings settings = settingsService.getOrCreateSettings();
        invoice.getItems().clear();
        populateAndCalculateItems(invoice, customer, settings, dto.getItems(), discountRate);

        Invoice updated = invoiceRepository.save(invoice);
        return toDTO(updated);
    }

    @Transactional
    public InvoiceDTO updateInvoiceStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        invoice.setStatus(status);
        Invoice updated = invoiceRepository.save(invoice);
        return toDTO(updated);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }

    public synchronized String generateNextInvoiceNumber() {
        int year = Year.now().getValue();
        long nextSeq = invoiceRepository.count() + 1;
        String candidate = String.format("INV-%d-%04d", year, nextSeq);
        int offset = 1;
        while (invoiceRepository.existsByInvoiceNumber(candidate)) {
            candidate = String.format("INV-%d-%04d", year, nextSeq + offset);
            offset++;
        }
        return candidate;
    }

    private void populateAndCalculateItems(Invoice invoice, Customer customer, BusinessSettings settings,
                                           List<InvoiceItemDTO> itemDTOs, BigDecimal discountRate) {
        boolean isIntra = GstUtil.isIntraState(settings.getState(), customer.getState());
        invoice.setIsInterState(!isIntra);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;

        for (InvoiceItemDTO itemDTO : itemDTOs) {
            BigDecimal qty = BigDecimal.valueOf(itemDTO.getQuantity());
            BigDecimal unitPrice = itemDTO.getUnitPrice().setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = unitPrice.multiply(qty).setScale(2, RoundingMode.HALF_UP);

            BigDecimal itemTaxRate = itemDTO.getTaxRate() != null ? itemDTO.getTaxRate() : BigDecimal.ZERO;
            BigDecimal itemTax = lineTotal.multiply(itemTaxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal cgst = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal sgst = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal igst = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

            if (isIntra) {
                cgst = itemTax.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                sgst = itemTax.subtract(cgst);
            } else {
                igst = itemTax;
            }

            subtotal = subtotal.add(lineTotal);
            totalCgst = totalCgst.add(cgst);
            totalSgst = totalSgst.add(sgst);
            totalIgst = totalIgst.add(igst);

            Product product = null;
            if (itemDTO.getProductId() != null) {
                product = productRepository.findById(itemDTO.getProductId()).orElse(null);
            }

            String hsn = itemDTO.getHsnCode();
            if ((hsn == null || hsn.trim().isEmpty()) && product != null) {
                hsn = product.getHsnCode();
            }

            InvoiceItem item = InvoiceItem.builder()
                    .description(itemDTO.getDescription().trim())
                    .hsnCode(hsn)
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(unitPrice)
                    .taxRate(itemTaxRate)
                    .taxAmount(itemTax)
                    .cgstAmount(cgst)
                    .sgstAmount(sgst)
                    .igstAmount(igst)
                    .lineTotal(lineTotal)
                    .product(product)
                    .build();

            invoice.addItem(item);
        }

        BigDecimal discountAmount = subtotal.multiply(discountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = totalCgst.add(totalSgst).add(totalIgst);
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(taxAmount);

        invoice.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        invoice.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        invoice.setCgstAmount(totalCgst.setScale(2, RoundingMode.HALF_UP));
        invoice.setSgstAmount(totalSgst.setScale(2, RoundingMode.HALF_UP));
        invoice.setIgstAmount(totalIgst.setScale(2, RoundingMode.HALF_UP));
        invoice.setTaxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP));
        invoice.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
    }

    public InvoiceDTO toDTO(Invoice invoice) {
        Customer c = invoice.getCustomer();
        BusinessSettings settings = settingsService.getOrCreateSettings();

        boolean isInter = Boolean.TRUE.equals(invoice.getIsInterState());

        List<InvoiceItemDTO> itemDTOs = invoice.getItems().stream()
                .map(item -> {
                    BigDecimal tax = item.getTaxAmount() != null ? item.getTaxAmount() : BigDecimal.ZERO;
                    BigDecimal cgst = item.getCgstAmount() != null ? item.getCgstAmount() : (isInter ? BigDecimal.ZERO : tax.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP));
                    BigDecimal sgst = item.getSgstAmount() != null ? item.getSgstAmount() : (isInter ? BigDecimal.ZERO : tax.subtract(cgst));
                    BigDecimal igst = item.getIgstAmount() != null ? item.getIgstAmount() : (isInter ? tax : BigDecimal.ZERO);

                    return InvoiceItemDTO.builder()
                            .id(item.getId())
                            .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                            .description(item.getDescription())
                            .hsnCode(item.getHsnCode())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .taxRate(item.getTaxRate())
                            .taxAmount(tax)
                            .cgstAmount(cgst)
                            .sgstAmount(sgst)
                            .igstAmount(igst)
                            .lineTotal(item.getLineTotal())
                            .build();
                })
                .collect(Collectors.toList());

        // Automatically mark as OVERDUE if overdue and not paid
        InvoiceStatus effectiveStatus = invoice.getStatus();
        if (effectiveStatus != InvoiceStatus.PAID && invoice.getDueDate().isBefore(LocalDate.now())) {
            effectiveStatus = InvoiceStatus.OVERDUE;
        }

        // Amount in words
        String amountInWords = IndianCurrencyUtil.toWords(invoice.getTotalAmount());

        // UPI QR Code & Link
        String upiUrl = null;
        String upiQrBase64 = null;
        if (settings.getUpiId() != null && !settings.getUpiId().trim().isEmpty()) {
            String payee = settings.getPayeeName() != null && !settings.getPayeeName().trim().isEmpty()
                    ? settings.getPayeeName()
                    : settings.getCompanyName();
            upiUrl = QrCodeUtil.buildUpiUrl(settings.getUpiId(), payee, invoice.getTotalAmount(), invoice.getInvoiceNumber());
            upiQrBase64 = QrCodeUtil.generateQrBase64(upiUrl, 200, 200);
        }

        // Tax Summary Breakdown grouped by Tax Rate
        Map<BigDecimal, List<InvoiceItemDTO>> byRate = itemDTOs.stream()
                .collect(Collectors.groupingBy(item -> item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO));

        List<TaxSummaryItemDTO> taxSummary = new ArrayList<>();
        byRate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    BigDecimal rate = entry.getKey();
                    BigDecimal taxable = entry.getValue().stream()
                            .map(InvoiceItemDTO::getLineTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal cgst = entry.getValue().stream()
                            .map(InvoiceItemDTO::getCgstAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal sgst = entry.getValue().stream()
                            .map(InvoiceItemDTO::getSgstAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal igst = entry.getValue().stream()
                            .map(InvoiceItemDTO::getIgstAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalTax = cgst.add(sgst).add(igst);

                    BigDecimal halfRate = rate.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

                    TaxSummaryItemDTO summaryItem = TaxSummaryItemDTO.builder()
                            .gstRate(rate)
                            .taxableAmount(taxable)
                            .cgstRate(isInter ? BigDecimal.ZERO : halfRate)
                            .cgstAmount(cgst)
                            .sgstRate(isInter ? BigDecimal.ZERO : halfRate)
                            .sgstAmount(sgst)
                            .igstRate(isInter ? rate : BigDecimal.ZERO)
                            .igstAmount(igst)
                            .totalTax(totalTax)
                            .build();

                    taxSummary.add(summaryItem);
                });

        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerId(c.getId())
                .customerName(c.getName())
                .customerEmail(c.getEmail())
                .customerPhone(c.getPhone())
                .customerAddress(c.getAddress())
                .customerCity(c.getCity())
                .customerState(c.getState())
                .customerPostalCode(c.getPostalCode())
                .customerCountry(c.getCountry())
                .customerGstin(c.getGstin())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .status(effectiveStatus)
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .cgstAmount(invoice.getCgstAmount() != null ? invoice.getCgstAmount() : (isInter ? BigDecimal.ZERO : invoice.getTaxAmount().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)))
                .sgstAmount(invoice.getSgstAmount() != null ? invoice.getSgstAmount() : (isInter ? BigDecimal.ZERO : invoice.getTaxAmount().subtract(invoice.getTaxAmount().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP))))
                .igstAmount(invoice.getIgstAmount() != null ? invoice.getIgstAmount() : (isInter ? invoice.getTaxAmount() : BigDecimal.ZERO))
                .isInterState(isInter)
                .discountRate(invoice.getDiscountRate())
                .discountAmount(invoice.getDiscountAmount())
                .totalAmount(invoice.getTotalAmount())
                .amountInWords(amountInWords)
                .upiPaymentUrl(upiUrl)
                .upiQrCodeBase64(upiQrBase64)
                .formattedTotal(IndianCurrencyUtil.format(invoice.getTotalAmount()))
                .formattedSubtotal(IndianCurrencyUtil.format(invoice.getSubtotal()))
                .formattedTaxAmount(IndianCurrencyUtil.format(invoice.getTaxAmount()))
                .formattedDiscountAmount(IndianCurrencyUtil.format(invoice.getDiscountAmount()))
                .notes(invoice.getNotes())
                .templateName(sanitizeTemplateName(invoice.getTemplateName()))
                .items(itemDTOs)
                .taxSummary(taxSummary)
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}
