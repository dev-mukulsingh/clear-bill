package com.clearbillsolutions.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Line item on an invoice.
 */
@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotBlank(message = "Item description is required")
    @Column(nullable = false)
    private String description;

    @Column(name = "hsn_code")
    private String hsnCode;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price cannot be negative")
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @NotNull(message = "Tax rate is required")
    @DecimalMin(value = "0.00", message = "Tax rate cannot be negative")
    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "cgst_amount", precision = 12, scale = 2)
    private BigDecimal cgstAmount;

    @Column(name = "sgst_amount", precision = 12, scale = 2)
    private BigDecimal sgstAmount;

    @Column(name = "igst_amount", precision = 12, scale = 2)
    private BigDecimal igstAmount;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    public InvoiceItem() {
    }

    public InvoiceItem(Long id, Invoice invoice, Product product, String description, String hsnCode, Integer quantity, BigDecimal unitPrice, BigDecimal taxRate, BigDecimal taxAmount, BigDecimal cgstAmount, BigDecimal sgstAmount, BigDecimal igstAmount, BigDecimal lineTotal) {
        this.id = id;
        this.invoice = invoice;
        this.product = product;
        this.description = description;
        this.hsnCode = hsnCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.cgstAmount = cgstAmount;
        this.sgstAmount = sgstAmount;
        this.igstAmount = igstAmount;
        this.lineTotal = lineTotal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }

    public BigDecimal getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }

    public BigDecimal getIgstAmount() { return igstAmount; }
    public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Invoice invoice;
        private Product product;
        private String description;
        private String hsnCode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;
        private BigDecimal cgstAmount;
        private BigDecimal sgstAmount;
        private BigDecimal igstAmount;
        private BigDecimal lineTotal;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder invoice(Invoice invoice) { this.invoice = invoice; return this; }
        public Builder product(Product product) { this.product = product; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder hsnCode(String hsnCode) { this.hsnCode = hsnCode; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder taxRate(BigDecimal taxRate) { this.taxRate = taxRate; return this; }
        public Builder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public Builder cgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; return this; }
        public Builder sgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; return this; }
        public Builder igstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; return this; }
        public Builder lineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; return this; }

        public InvoiceItem build() {
            return new InvoiceItem(id, invoice, product, description, hsnCode, quantity, unitPrice, taxRate, taxAmount, cgstAmount, sgstAmount, igstAmount, lineTotal);
        }
    }
}
