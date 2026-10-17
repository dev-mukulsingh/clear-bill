package com.clearbillsolutions.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO holding data for Dashboard Chart.js visualizations (Monthly/Yearly Invoiced vs Paid, and Status Doughnut).
 */
public class DashboardChartsDTO {
    private List<String> monthlyLabels;
    private List<BigDecimal> monthlyInvoiced;
    private List<BigDecimal> monthlyPaid;

    private List<String> yearlyLabels;
    private List<BigDecimal> yearlyInvoiced;
    private List<BigDecimal> yearlyPaid;

    private List<String> statusLabels;
    private List<BigDecimal> statusAmounts;
    private List<String> statusColors;
    private boolean hasData;

    public DashboardChartsDTO() {}

    public DashboardChartsDTO(List<String> monthlyLabels, List<BigDecimal> monthlyInvoiced, List<BigDecimal> monthlyPaid,
                              List<String> yearlyLabels, List<BigDecimal> yearlyInvoiced, List<BigDecimal> yearlyPaid,
                              List<String> statusLabels, List<BigDecimal> statusAmounts, List<String> statusColors, boolean hasData) {
        this.monthlyLabels = monthlyLabels;
        this.monthlyInvoiced = monthlyInvoiced;
        this.monthlyPaid = monthlyPaid;
        this.yearlyLabels = yearlyLabels;
        this.yearlyInvoiced = yearlyInvoiced;
        this.yearlyPaid = yearlyPaid;
        this.statusLabels = statusLabels;
        this.statusAmounts = statusAmounts;
        this.statusColors = statusColors;
        this.hasData = hasData;
    }

    public List<String> getMonthlyLabels() { return monthlyLabels; }
    public void setMonthlyLabels(List<String> monthlyLabels) { this.monthlyLabels = monthlyLabels; }

    public List<BigDecimal> getMonthlyInvoiced() { return monthlyInvoiced; }
    public void setMonthlyInvoiced(List<BigDecimal> monthlyInvoiced) { this.monthlyInvoiced = monthlyInvoiced; }

    public List<BigDecimal> getMonthlyPaid() { return monthlyPaid; }
    public void setMonthlyPaid(List<BigDecimal> monthlyPaid) { this.monthlyPaid = monthlyPaid; }

    // Backward compatibility helper
    public List<BigDecimal> getMonthlyRevenue() { return monthlyInvoiced; }

    public List<String> getYearlyLabels() { return yearlyLabels; }
    public void setYearlyLabels(List<String> yearlyLabels) { this.yearlyLabels = yearlyLabels; }

    public List<BigDecimal> getYearlyInvoiced() { return yearlyInvoiced; }
    public void setYearlyInvoiced(List<BigDecimal> yearlyInvoiced) { this.yearlyInvoiced = yearlyInvoiced; }

    public List<BigDecimal> getYearlyPaid() { return yearlyPaid; }
    public void setYearlyPaid(List<BigDecimal> yearlyPaid) { this.yearlyPaid = yearlyPaid; }

    public List<String> getStatusLabels() { return statusLabels; }
    public void setStatusLabels(List<String> statusLabels) { this.statusLabels = statusLabels; }

    public List<BigDecimal> getStatusAmounts() { return statusAmounts; }
    public void setStatusAmounts(List<BigDecimal> statusAmounts) { this.statusAmounts = statusAmounts; }

    public List<String> getStatusColors() { return statusColors; }
    public void setStatusColors(List<String> statusColors) { this.statusColors = statusColors; }

    public boolean isHasData() { return hasData; }
    public void setHasData(boolean hasData) { this.hasData = hasData; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private List<String> monthlyLabels;
        private List<BigDecimal> monthlyInvoiced;
        private List<BigDecimal> monthlyPaid;
        private List<String> yearlyLabels;
        private List<BigDecimal> yearlyInvoiced;
        private List<BigDecimal> yearlyPaid;
        private List<String> statusLabels;
        private List<BigDecimal> statusAmounts;
        private List<String> statusColors;
        private boolean hasData;

        public Builder monthlyLabels(List<String> monthlyLabels) { this.monthlyLabels = monthlyLabels; return this; }
        public Builder monthlyInvoiced(List<BigDecimal> monthlyInvoiced) { this.monthlyInvoiced = monthlyInvoiced; return this; }
        public Builder monthlyPaid(List<BigDecimal> monthlyPaid) { this.monthlyPaid = monthlyPaid; return this; }
        public Builder yearlyLabels(List<String> yearlyLabels) { this.yearlyLabels = yearlyLabels; return this; }
        public Builder yearlyInvoiced(List<BigDecimal> yearlyInvoiced) { this.yearlyInvoiced = yearlyInvoiced; return this; }
        public Builder yearlyPaid(List<BigDecimal> yearlyPaid) { this.yearlyPaid = yearlyPaid; return this; }
        public Builder statusLabels(List<String> statusLabels) { this.statusLabels = statusLabels; return this; }
        public Builder statusAmounts(List<BigDecimal> statusAmounts) { this.statusAmounts = statusAmounts; return this; }
        public Builder statusColors(List<String> statusColors) { this.statusColors = statusColors; return this; }
        public Builder hasData(boolean hasData) { this.hasData = hasData; return this; }

        public DashboardChartsDTO build() {
            return new DashboardChartsDTO(monthlyLabels, monthlyInvoiced, monthlyPaid,
                    yearlyLabels, yearlyInvoiced, yearlyPaid,
                    statusLabels, statusAmounts, statusColors, hasData);
        }
    }
}
