package com.clearbillsolutions.dto;

import com.clearbillsolutions.entity.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private String customerCity;
    private String customerState;
    private String customerPostalCode;
    private String customerCountry;
    private String customerGstin;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private InvoiceStatus status;

    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;
    private Boolean isInterState = false;

    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private String amountInWords;
    private String upiQrCodeBase64;
    private String upiPaymentUrl;

    private String formattedTotal;
    private String formattedSubtotal;
    private String formattedTaxAmount;
    private String formattedDiscountAmount;

    private String notes;

    private String templateName = "red";

    @NotEmpty(message = "At least one invoice item is required")
    @Valid
    private List<InvoiceItemDTO> items = new ArrayList<>();

    private List<TaxSummaryItemDTO> taxSummary = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InvoiceDTO() {
    }

    public InvoiceDTO(Long id, String invoiceNumber, Long customerId, String customerName, String customerEmail, String customerPhone, String customerAddress, String customerCity, String customerState, String customerPostalCode, String customerCountry, String customerGstin, LocalDate issueDate, LocalDate dueDate, InvoiceStatus status, BigDecimal subtotal, BigDecimal taxAmount, BigDecimal cgstAmount, BigDecimal sgstAmount, BigDecimal igstAmount, Boolean isInterState, BigDecimal discountRate, BigDecimal discountAmount, BigDecimal totalAmount, String amountInWords, String upiQrCodeBase64, String upiPaymentUrl, String formattedTotal, String formattedSubtotal, String formattedTaxAmount, String formattedDiscountAmount, String notes, String templateName, List<InvoiceItemDTO> items, List<TaxSummaryItemDTO> taxSummary, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.customerCity = customerCity;
        this.customerState = customerState;
        this.customerPostalCode = customerPostalCode;
        this.customerCountry = customerCountry;
        this.customerGstin = customerGstin;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.cgstAmount = cgstAmount;
        this.sgstAmount = sgstAmount;
        this.igstAmount = igstAmount;
        this.isInterState = isInterState != null ? isInterState : false;
        this.discountRate = discountRate;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.amountInWords = amountInWords;
        this.upiQrCodeBase64 = upiQrCodeBase64;
        this.upiPaymentUrl = upiPaymentUrl;
        this.formattedTotal = formattedTotal;
        this.formattedSubtotal = formattedSubtotal;
        this.formattedTaxAmount = formattedTaxAmount;
        this.formattedDiscountAmount = formattedDiscountAmount;
        this.notes = notes;
        this.templateName = templateName != null ? templateName : "red";
        this.items = items != null ? items : new ArrayList<>();
        this.taxSummary = taxSummary != null ? taxSummary : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }
    public String getCustomerState() { return customerState; }
    public void setCustomerState(String customerState) { this.customerState = customerState; }
    public String getCustomerPostalCode() { return customerPostalCode; }
    public void setCustomerPostalCode(String customerPostalCode) { this.customerPostalCode = customerPostalCode; }
    public String getCustomerCountry() { return customerCountry; }
    public void setCustomerCountry(String customerCountry) { this.customerCountry = customerCountry; }
    public String getCustomerGstin() { return customerGstin; }
    public void setCustomerGstin(String customerGstin) { this.customerGstin = customerGstin; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }
    public BigDecimal getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }
    public BigDecimal getIgstAmount() { return igstAmount; }
    public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }
    public Boolean getIsInterState() { return isInterState; }
    public void setIsInterState(Boolean interState) { isInterState = interState; }
    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getAmountInWords() { return amountInWords; }
    public void setAmountInWords(String amountInWords) { this.amountInWords = amountInWords; }
    public String getUpiQrCodeBase64() { return upiQrCodeBase64; }
    public void setUpiQrCodeBase64(String upiQrCodeBase64) { this.upiQrCodeBase64 = upiQrCodeBase64; }
    public String getUpiPaymentUrl() { return upiPaymentUrl; }
    public void setUpiPaymentUrl(String upiPaymentUrl) { this.upiPaymentUrl = upiPaymentUrl; }
    public String getFormattedTotal() { return formattedTotal; }
    public void setFormattedTotal(String formattedTotal) { this.formattedTotal = formattedTotal; }
    public String getFormattedSubtotal() { return formattedSubtotal; }
    public void setFormattedSubtotal(String formattedSubtotal) { this.formattedSubtotal = formattedSubtotal; }
    public String getFormattedTaxAmount() { return formattedTaxAmount; }
    public void setFormattedTaxAmount(String formattedTaxAmount) { this.formattedTaxAmount = formattedTaxAmount; }
    public String getFormattedDiscountAmount() { return formattedDiscountAmount; }
    public void setFormattedDiscountAmount(String formattedDiscountAmount) { this.formattedDiscountAmount = formattedDiscountAmount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
    public List<TaxSummaryItemDTO> getTaxSummary() { return taxSummary; }
    public void setTaxSummary(List<TaxSummaryItemDTO> taxSummary) { this.taxSummary = taxSummary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String invoiceNumber;
        private Long customerId;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String customerAddress;
        private String customerCity;
        private String customerState;
        private String customerPostalCode;
        private String customerCountry;
        private String customerGstin;
        private LocalDate issueDate;
        private LocalDate dueDate;
        private InvoiceStatus status;
        private BigDecimal subtotal;
        private BigDecimal taxAmount;
        private BigDecimal cgstAmount;
        private BigDecimal sgstAmount;
        private BigDecimal igstAmount;
        private Boolean isInterState = false;
        private BigDecimal discountRate;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private String amountInWords;
        private String upiQrCodeBase64;
        private String upiPaymentUrl;
        private String formattedTotal;
        private String formattedSubtotal;
        private String formattedTaxAmount;
        private String formattedDiscountAmount;
        private String notes;
        private String templateName = "red";
        private List<InvoiceItemDTO> items = new ArrayList<>();
        private List<TaxSummaryItemDTO> taxSummary = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder customerId(Long customerId) { this.customerId = customerId; return this; }
        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public Builder customerPhone(String customerPhone) { this.customerPhone = customerPhone; return this; }
        public Builder customerAddress(String customerAddress) { this.customerAddress = customerAddress; return this; }
        public Builder customerCity(String customerCity) { this.customerCity = customerCity; return this; }
        public Builder customerState(String customerState) { this.customerState = customerState; return this; }
        public Builder customerPostalCode(String customerPostalCode) { this.customerPostalCode = customerPostalCode; return this; }
        public Builder customerCountry(String customerCountry) { this.customerCountry = customerCountry; return this; }
        public Builder customerGstin(String customerGstin) { this.customerGstin = customerGstin; return this; }
        public Builder issueDate(LocalDate issueDate) { this.issueDate = issueDate; return this; }
        public Builder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public Builder status(InvoiceStatus status) { this.status = status; return this; }
        public Builder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public Builder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public Builder cgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; return this; }
        public Builder sgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; return this; }
        public Builder igstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; return this; }
        public Builder isInterState(Boolean isInterState) { this.isInterState = isInterState; return this; }
        public Builder discountRate(BigDecimal discountRate) { this.discountRate = discountRate; return this; }
        public Builder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder amountInWords(String amountInWords) { this.amountInWords = amountInWords; return this; }
        public Builder upiQrCodeBase64(String upiQrCodeBase64) { this.upiQrCodeBase64 = upiQrCodeBase64; return this; }
        public Builder upiPaymentUrl(String upiPaymentUrl) { this.upiPaymentUrl = upiPaymentUrl; return this; }
        public Builder formattedTotal(String formattedTotal) { this.formattedTotal = formattedTotal; return this; }
        public Builder formattedSubtotal(String formattedSubtotal) { this.formattedSubtotal = formattedSubtotal; return this; }
        public Builder formattedTaxAmount(String formattedTaxAmount) { this.formattedTaxAmount = formattedTaxAmount; return this; }
        public Builder formattedDiscountAmount(String formattedDiscountAmount) { this.formattedDiscountAmount = formattedDiscountAmount; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder templateName(String templateName) { this.templateName = templateName; return this; }
        public Builder items(List<InvoiceItemDTO> items) { this.items = items; return this; }
        public Builder taxSummary(List<TaxSummaryItemDTO> taxSummary) { this.taxSummary = taxSummary; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public InvoiceDTO build() {
            return new InvoiceDTO(id, invoiceNumber, customerId, customerName, customerEmail, customerPhone, customerAddress, customerCity, customerState, customerPostalCode, customerCountry, customerGstin, issueDate, dueDate, status, subtotal, taxAmount, cgstAmount, sgstAmount, igstAmount, isInterState, discountRate, discountAmount, totalAmount, amountInWords, upiQrCodeBase64, upiPaymentUrl, formattedTotal, formattedSubtotal, formattedTaxAmount, formattedDiscountAmount, notes, templateName, items, taxSummary, createdAt, updatedAt);
        }
    }
}
