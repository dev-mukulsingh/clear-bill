package com.clearbillsolutions.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class InvoiceItemDTO {
    private Long id;
    private Long productId;

    @NotBlank(message = "Item description is required")
    private String description;

    private String hsnCode;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    @NotNull(message = "Tax rate is required")
    @DecimalMin(value = "0.00", message = "Tax rate cannot be negative")
    private BigDecimal taxRate;

    private BigDecimal taxAmount;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;
    private BigDecimal lineTotal;

    public InvoiceItemDTO() {
    }

    public InvoiceItemDTO(Long id, Long productId, String description, String hsnCode, Integer quantity, BigDecimal unitPrice, BigDecimal taxRate, BigDecimal taxAmount, BigDecimal cgstAmount, BigDecimal sgstAmount, BigDecimal igstAmount, BigDecimal lineTotal) {
        this.id = id;
        this.productId = productId;
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
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
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
    public BigDecimal getGstRate() { return taxRate; }
    public void setGstRate(BigDecimal gstRate) { this.taxRate = gstRate; }
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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
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
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder hsnCode(String hsnCode) { this.hsnCode = hsnCode; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder taxRate(BigDecimal taxRate) { this.taxRate = taxRate; return this; }
        public Builder gstRate(BigDecimal gstRate) { this.taxRate = gstRate; return this; }
        public Builder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public Builder cgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; return this; }
        public Builder sgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; return this; }
        public Builder igstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; return this; }
        public Builder lineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; return this; }

        public InvoiceItemDTO build() {
            return new InvoiceItemDTO(id, productId, description, hsnCode, quantity, unitPrice, taxRate, taxAmount, cgstAmount, sgstAmount, igstAmount, lineTotal);
        }
    }
}
