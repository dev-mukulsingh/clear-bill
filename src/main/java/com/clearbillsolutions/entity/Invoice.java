package com.clearbillsolutions.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoice entity representing a billing transaction with multiple line items.
 */
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Invoice number is required")
    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull(message = "Issue date is required")
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull(message = "Invoice status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvoiceStatus status;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "cgst_amount", precision = 12, scale = 2)
    private BigDecimal cgstAmount;

    @Column(name = "sgst_amount", precision = 12, scale = 2)
    private BigDecimal sgstAmount;

    @Column(name = "igst_amount", precision = 12, scale = 2)
    private BigDecimal igstAmount;

    @Column(name = "is_inter_state")
    private Boolean isInterState = false;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "discount_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountRate;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @NotBlank(message = "Template name is required")
    @Column(name = "template_name", nullable = false, length = 50)
    private String templateName = "red";

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<InvoiceItem> items = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Invoice() {
    }

    public Invoice(Long id, String invoiceNumber, Customer customer, LocalDate issueDate, LocalDate dueDate, InvoiceStatus status, BigDecimal subtotal, BigDecimal taxAmount, BigDecimal cgstAmount, BigDecimal sgstAmount, BigDecimal igstAmount, Boolean isInterState, BigDecimal discountRate, BigDecimal discountAmount, BigDecimal totalAmount, String notes, String templateName, List<InvoiceItem> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.customer = customer;
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
        this.notes = notes;
        this.templateName = templateName != null ? templateName : "red";
        this.items = items != null ? items : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addItem(InvoiceItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        item.setInvoice(this);
    }

    public void removeItem(InvoiceItem item) {
        if (items != null) {
            items.remove(item);
            item.setInvoice(null);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = InvoiceStatus.DRAFT;
        }
        if (this.templateName == null || this.templateName.trim().isEmpty()) {
            this.templateName = "red";
        }
        if (this.discountRate == null) {
            this.discountRate = BigDecimal.ZERO;
        }
        if (this.discountAmount == null) {
            this.discountAmount = BigDecimal.ZERO;
        }
        if (this.isInterState == null) {
            this.isInterState = false;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String invoiceNumber;
        private Customer customer;
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
        private String notes;
        private String templateName = "red";
        private List<InvoiceItem> items = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder customer(Customer customer) { this.customer = customer; return this; }
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
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder templateName(String templateName) { this.templateName = templateName; return this; }
        public Builder items(List<InvoiceItem> items) { this.items = items; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Invoice build() {
            return new Invoice(id, invoiceNumber, customer, issueDate, dueDate, status, subtotal, taxAmount, cgstAmount, sgstAmount, igstAmount, isInterState, discountRate, discountAmount, totalAmount, notes, templateName, items, createdAt, updatedAt);
        }
    }
}
