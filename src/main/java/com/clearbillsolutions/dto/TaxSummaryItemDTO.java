package com.clearbillsolutions.dto;

import java.math.BigDecimal;

/**
 * Summary breakdown of GST for each HSN/tax rate group.
 */
public class TaxSummaryItemDTO {
    private String hsnCode;
    private BigDecimal gstRate;
    private BigDecimal taxableAmount;
    private BigDecimal cgstRate;
    private BigDecimal cgstAmount;
    private BigDecimal sgstRate;
    private BigDecimal sgstAmount;
    private BigDecimal igstRate;
    private BigDecimal igstAmount;
    private BigDecimal totalTax;

    public TaxSummaryItemDTO() {
    }

    public TaxSummaryItemDTO(String hsnCode, BigDecimal gstRate, BigDecimal taxableAmount, BigDecimal cgstRate, BigDecimal cgstAmount, BigDecimal sgstRate, BigDecimal sgstAmount, BigDecimal igstRate, BigDecimal igstAmount, BigDecimal totalTax) {
        this.hsnCode = hsnCode;
        this.gstRate = gstRate;
        this.taxableAmount = taxableAmount;
        this.cgstRate = cgstRate;
        this.cgstAmount = cgstAmount;
        this.sgstRate = sgstRate;
        this.sgstAmount = sgstAmount;
        this.igstRate = igstRate;
        this.igstAmount = igstAmount;
        this.totalTax = totalTax;
    }

    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }

    public BigDecimal getGstRate() { return gstRate; }
    public void setGstRate(BigDecimal gstRate) { this.gstRate = gstRate; }

    public BigDecimal getTaxableAmount() { return taxableAmount; }
    public void setTaxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; }

    public BigDecimal getCgstRate() { return cgstRate; }
    public void setCgstRate(BigDecimal cgstRate) { this.cgstRate = cgstRate; }

    public BigDecimal getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }

    public BigDecimal getSgstRate() { return sgstRate; }
    public void setSgstRate(BigDecimal sgstRate) { this.sgstRate = sgstRate; }

    public BigDecimal getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }

    public BigDecimal getIgstRate() { return igstRate; }
    public void setIgstRate(BigDecimal igstRate) { this.igstRate = igstRate; }

    public BigDecimal getIgstAmount() { return igstAmount; }
    public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }

    public BigDecimal getTotalTax() { return totalTax; }
    public void setTotalTax(BigDecimal totalTax) { this.totalTax = totalTax; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String hsnCode;
        private BigDecimal gstRate;
        private BigDecimal taxableAmount;
        private BigDecimal cgstRate;
        private BigDecimal cgstAmount;
        private BigDecimal sgstRate;
        private BigDecimal sgstAmount;
        private BigDecimal igstRate;
        private BigDecimal igstAmount;
        private BigDecimal totalTax;

        public Builder hsnCode(String hsnCode) { this.hsnCode = hsnCode; return this; }
        public Builder gstRate(BigDecimal gstRate) { this.gstRate = gstRate; return this; }
        public Builder taxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; return this; }
        public Builder cgstRate(BigDecimal cgstRate) { this.cgstRate = cgstRate; return this; }
        public Builder cgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; return this; }
        public Builder sgstRate(BigDecimal sgstRate) { this.sgstRate = sgstRate; return this; }
        public Builder sgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; return this; }
        public Builder igstRate(BigDecimal igstRate) { this.igstRate = igstRate; return this; }
        public Builder igstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; return this; }
        public Builder totalTax(BigDecimal totalTax) { this.totalTax = totalTax; return this; }

        public TaxSummaryItemDTO build() {
            return new TaxSummaryItemDTO(hsnCode, gstRate, taxableAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, igstRate, igstAmount, totalTax);
        }
    }
}
