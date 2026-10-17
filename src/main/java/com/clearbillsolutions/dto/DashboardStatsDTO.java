package com.clearbillsolutions.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data transfer object for Dashboard overview statistics and sparklines.
 */
public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private BigDecimal paidAmount;
    private BigDecimal unpaidAmount;
    private BigDecimal pendingAmount;
    private BigDecimal overdueAmount;

    private Double revenueChangePercent;
    private Double paidChangePercent;
    private Double pendingChangePercent;
    private Double overdueChangePercent;

    private List<BigDecimal> sparklineRevenue;
    private List<BigDecimal> sparklinePaid;
    private List<BigDecimal> sparklinePending;
    private List<BigDecimal> sparklineOverdue;

    private long totalInvoices;
    private long paidInvoicesCount;
    private long sentInvoicesCount;
    private long draftInvoicesCount;
    private long overdueInvoicesCount;
    private long totalCustomers;
    private long totalProducts;
    private List<InvoiceDTO> recentInvoices;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(BigDecimal totalRevenue, BigDecimal paidAmount, BigDecimal unpaidAmount,
                             BigDecimal pendingAmount, BigDecimal overdueAmount,
                             Double revenueChangePercent, Double paidChangePercent,
                             Double pendingChangePercent, Double overdueChangePercent,
                             List<BigDecimal> sparklineRevenue, List<BigDecimal> sparklinePaid,
                             List<BigDecimal> sparklinePending, List<BigDecimal> sparklineOverdue,
                             long totalInvoices, long paidInvoicesCount, long sentInvoicesCount,
                             long draftInvoicesCount, long overdueInvoicesCount,
                             long totalCustomers, long totalProducts, List<InvoiceDTO> recentInvoices) {
        this.totalRevenue = totalRevenue;
        this.paidAmount = paidAmount;
        this.unpaidAmount = unpaidAmount;
        this.pendingAmount = pendingAmount;
        this.overdueAmount = overdueAmount;
        this.revenueChangePercent = revenueChangePercent;
        this.paidChangePercent = paidChangePercent;
        this.pendingChangePercent = pendingChangePercent;
        this.overdueChangePercent = overdueChangePercent;
        this.sparklineRevenue = sparklineRevenue;
        this.sparklinePaid = sparklinePaid;
        this.sparklinePending = sparklinePending;
        this.sparklineOverdue = sparklineOverdue;
        this.totalInvoices = totalInvoices;
        this.paidInvoicesCount = paidInvoicesCount;
        this.sentInvoicesCount = sentInvoicesCount;
        this.draftInvoicesCount = draftInvoicesCount;
        this.overdueInvoicesCount = overdueInvoicesCount;
        this.totalCustomers = totalCustomers;
        this.totalProducts = totalProducts;
        this.recentInvoices = recentInvoices;
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getUnpaidAmount() { return unpaidAmount; }
    public void setUnpaidAmount(BigDecimal unpaidAmount) { this.unpaidAmount = unpaidAmount; }

    public BigDecimal getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; }

    public BigDecimal getOverdueAmount() { return overdueAmount; }
    public void setOverdueAmount(BigDecimal overdueAmount) { this.overdueAmount = overdueAmount; }

    public Double getRevenueChangePercent() { return revenueChangePercent; }
    public void setRevenueChangePercent(Double revenueChangePercent) { this.revenueChangePercent = revenueChangePercent; }

    public Double getPaidChangePercent() { return paidChangePercent; }
    public void setPaidChangePercent(Double paidChangePercent) { this.paidChangePercent = paidChangePercent; }

    public Double getPendingChangePercent() { return pendingChangePercent; }
    public void setPendingChangePercent(Double pendingChangePercent) { this.pendingChangePercent = pendingChangePercent; }

    public Double getOverdueChangePercent() { return overdueChangePercent; }
    public void setOverdueChangePercent(Double overdueChangePercent) { this.overdueChangePercent = overdueChangePercent; }

    public List<BigDecimal> getSparklineRevenue() { return sparklineRevenue; }
    public void setSparklineRevenue(List<BigDecimal> sparklineRevenue) { this.sparklineRevenue = sparklineRevenue; }

    public List<BigDecimal> getSparklinePaid() { return sparklinePaid; }
    public void setSparklinePaid(List<BigDecimal> sparklinePaid) { this.sparklinePaid = sparklinePaid; }

    public List<BigDecimal> getSparklinePending() { return sparklinePending; }
    public void setSparklinePending(List<BigDecimal> sparklinePending) { this.sparklinePending = sparklinePending; }

    public List<BigDecimal> getSparklineOverdue() { return sparklineOverdue; }
    public void setSparklineOverdue(List<BigDecimal> sparklineOverdue) { this.sparklineOverdue = sparklineOverdue; }

    public long getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; }

    public long getPaidInvoicesCount() { return paidInvoicesCount; }
    public void setPaidInvoicesCount(long paidInvoicesCount) { this.paidInvoicesCount = paidInvoicesCount; }

    public long getSentInvoicesCount() { return sentInvoicesCount; }
    public void setSentInvoicesCount(long sentInvoicesCount) { this.sentInvoicesCount = sentInvoicesCount; }

    public long getDraftInvoicesCount() { return draftInvoicesCount; }
    public void setDraftInvoicesCount(long draftInvoicesCount) { this.draftInvoicesCount = draftInvoicesCount; }

    public long getOverdueInvoicesCount() { return overdueInvoicesCount; }
    public void setOverdueInvoicesCount(long overdueInvoicesCount) { this.overdueInvoicesCount = overdueInvoicesCount; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public List<InvoiceDTO> getRecentInvoices() { return recentInvoices; }
    public void setRecentInvoices(List<InvoiceDTO> recentInvoices) { this.recentInvoices = recentInvoices; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal totalRevenue;
        private BigDecimal paidAmount;
        private BigDecimal unpaidAmount;
        private BigDecimal pendingAmount;
        private BigDecimal overdueAmount;
        private Double revenueChangePercent;
        private Double paidChangePercent;
        private Double pendingChangePercent;
        private Double overdueChangePercent;
        private List<BigDecimal> sparklineRevenue;
        private List<BigDecimal> sparklinePaid;
        private List<BigDecimal> sparklinePending;
        private List<BigDecimal> sparklineOverdue;
        private long totalInvoices;
        private long paidInvoicesCount;
        private long sentInvoicesCount;
        private long draftInvoicesCount;
        private long overdueInvoicesCount;
        private long totalCustomers;
        private long totalProducts;
        private List<InvoiceDTO> recentInvoices;

        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder paidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; return this; }
        public Builder unpaidAmount(BigDecimal unpaidAmount) { this.unpaidAmount = unpaidAmount; return this; }
        public Builder pendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; return this; }
        public Builder overdueAmount(BigDecimal overdueAmount) { this.overdueAmount = overdueAmount; return this; }
        public Builder revenueChangePercent(Double val) { this.revenueChangePercent = val; return this; }
        public Builder paidChangePercent(Double val) { this.paidChangePercent = val; return this; }
        public Builder pendingChangePercent(Double val) { this.pendingChangePercent = val; return this; }
        public Builder overdueChangePercent(Double val) { this.overdueChangePercent = val; return this; }
        public Builder sparklineRevenue(List<BigDecimal> list) { this.sparklineRevenue = list; return this; }
        public Builder sparklinePaid(List<BigDecimal> list) { this.sparklinePaid = list; return this; }
        public Builder sparklinePending(List<BigDecimal> list) { this.sparklinePending = list; return this; }
        public Builder sparklineOverdue(List<BigDecimal> list) { this.sparklineOverdue = list; return this; }
        public Builder totalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; return this; }
        public Builder paidInvoicesCount(long paidInvoicesCount) { this.paidInvoicesCount = paidInvoicesCount; return this; }
        public Builder sentInvoicesCount(long sentInvoicesCount) { this.sentInvoicesCount = sentInvoicesCount; return this; }
        public Builder draftInvoicesCount(long draftInvoicesCount) { this.draftInvoicesCount = draftInvoicesCount; return this; }
        public Builder overdueInvoicesCount(long overdueInvoicesCount) { this.overdueInvoicesCount = overdueInvoicesCount; return this; }
        public Builder totalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; return this; }
        public Builder totalProducts(long totalProducts) { this.totalProducts = totalProducts; return this; }
        public Builder recentInvoices(List<InvoiceDTO> recentInvoices) { this.recentInvoices = recentInvoices; return this; }

        public DashboardStatsDTO build() {
            return new DashboardStatsDTO(totalRevenue, paidAmount, unpaidAmount, pendingAmount, overdueAmount,
                    revenueChangePercent, paidChangePercent, pendingChangePercent, overdueChangePercent,
                    sparklineRevenue, sparklinePaid, sparklinePending, sparklineOverdue,
                    totalInvoices, paidInvoicesCount, sentInvoicesCount, draftInvoicesCount, overdueInvoicesCount,
                    totalCustomers, totalProducts, recentInvoices);
        }
    }
}
