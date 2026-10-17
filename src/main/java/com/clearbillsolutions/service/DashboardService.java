package com.clearbillsolutions.service;

import com.clearbillsolutions.dto.DashboardChartsDTO;
import com.clearbillsolutions.dto.DashboardStatsDTO;
import com.clearbillsolutions.dto.InvoiceDTO;
import com.clearbillsolutions.entity.Invoice;
import com.clearbillsolutions.entity.InvoiceStatus;
import com.clearbillsolutions.repository.CustomerRepository;
import com.clearbillsolutions.repository.InvoiceRepository;
import com.clearbillsolutions.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InvoiceService invoiceService;

    public DashboardService(InvoiceRepository invoiceRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository,
                            InvoiceService invoiceService) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.invoiceService = invoiceService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        LocalDate today = LocalDate.now();
        List<Invoice> allInvoices = invoiceRepository.findAll();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        BigDecimal overdueAmount = BigDecimal.ZERO;

        for (Invoice inv : allInvoices) {
            BigDecimal amt = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
            totalRevenue = totalRevenue.add(amt);

            if (inv.getStatus() == InvoiceStatus.PAID) {
                paidAmount = paidAmount.add(amt);
            } else if (inv.getDueDate() != null && inv.getDueDate().isBefore(today)) {
                overdueAmount = overdueAmount.add(amt);
            } else {
                pendingAmount = pendingAmount.add(amt);
            }
        }

        BigDecimal unpaidAmount = pendingAmount.add(overdueAmount);

        // Calculate 6-month sparklines and % changes
        List<BigDecimal> sparklineRevenue = new ArrayList<>();
        List<BigDecimal> sparklinePaid = new ArrayList<>();
        List<BigDecimal> sparklinePending = new ArrayList<>();
        List<BigDecimal> sparklineOverdue = new ArrayList<>();

        YearMonth currentYM = YearMonth.from(today);
        YearMonth prevYM = currentYM.minusMonths(1);

        BigDecimal curMonthRevenue = BigDecimal.ZERO;
        BigDecimal prevMonthRevenue = BigDecimal.ZERO;
        BigDecimal curMonthPaid = BigDecimal.ZERO;
        BigDecimal prevMonthPaid = BigDecimal.ZERO;
        BigDecimal curMonthPending = BigDecimal.ZERO;
        BigDecimal prevMonthPending = BigDecimal.ZERO;
        BigDecimal curMonthOverdue = BigDecimal.ZERO;
        BigDecimal prevMonthOverdue = BigDecimal.ZERO;

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = currentYM.minusMonths(i);
            BigDecimal mRev = BigDecimal.ZERO;
            BigDecimal mPaid = BigDecimal.ZERO;
            BigDecimal mPending = BigDecimal.ZERO;
            BigDecimal mOverdue = BigDecimal.ZERO;

            for (Invoice inv : allInvoices) {
                if (inv.getIssueDate() != null && YearMonth.from(inv.getIssueDate()).equals(ym)) {
                    BigDecimal amt = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
                    mRev = mRev.add(amt);
                    if (inv.getStatus() == InvoiceStatus.PAID) {
                        mPaid = mPaid.add(amt);
                    } else if (inv.getDueDate() != null && inv.getDueDate().isBefore(today)) {
                        mOverdue = mOverdue.add(amt);
                    } else {
                        mPending = mPending.add(amt);
                    }
                }
            }
            sparklineRevenue.add(mRev);
            sparklinePaid.add(mPaid);
            sparklinePending.add(mPending);
            sparklineOverdue.add(mOverdue);

            if (ym.equals(currentYM)) {
                curMonthRevenue = mRev;
                curMonthPaid = mPaid;
                curMonthPending = mPending;
                curMonthOverdue = mOverdue;
            } else if (ym.equals(prevYM)) {
                prevMonthRevenue = mRev;
                prevMonthPaid = mPaid;
                prevMonthPending = mPending;
                prevMonthOverdue = mOverdue;
            }
        }

        Double revenueChange = computeChangePercent(curMonthRevenue, prevMonthRevenue);
        Double paidChange = computeChangePercent(curMonthPaid, prevMonthPaid);
        Double pendingChange = computeChangePercent(curMonthPending, prevMonthPending);
        Double overdueChange = computeChangePercent(curMonthOverdue, prevMonthOverdue);

        long totalInvoices = allInvoices.size();
        long paidCount = allInvoices.stream().filter(inv -> inv.getStatus() == InvoiceStatus.PAID).count();
        long sentCount = allInvoices.stream().filter(inv -> inv.getStatus() == InvoiceStatus.SENT).count();
        long draftCount = allInvoices.stream().filter(inv -> inv.getStatus() == InvoiceStatus.DRAFT).count();
        long overdueCount = allInvoices.stream()
                .filter(inv -> inv.getStatus() != InvoiceStatus.PAID && inv.getDueDate() != null && inv.getDueDate().isBefore(today))
                .count();

        long totalCustomers = customerRepository.count();
        long totalProducts = productRepository.count();

        List<InvoiceDTO> recentInvoices = invoiceRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(invoiceService::toDTO)
                .collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalRevenue(totalRevenue)
                .paidAmount(paidAmount)
                .unpaidAmount(unpaidAmount)
                .pendingAmount(pendingAmount)
                .overdueAmount(overdueAmount)
                .revenueChangePercent(revenueChange)
                .paidChangePercent(paidChange)
                .pendingChangePercent(pendingChange)
                .overdueChangePercent(overdueChange)
                .sparklineRevenue(sparklineRevenue)
                .sparklinePaid(sparklinePaid)
                .sparklinePending(sparklinePending)
                .sparklineOverdue(sparklineOverdue)
                .totalInvoices(totalInvoices)
                .paidInvoicesCount(paidCount)
                .sentInvoicesCount(sentCount)
                .draftInvoicesCount(draftCount)
                .overdueInvoicesCount(overdueCount)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .recentInvoices(recentInvoices)
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardChartsDTO getDashboardCharts() {
        LocalDate now = LocalDate.now();
        List<String> monthlyLabels = new ArrayList<>();
        List<BigDecimal> monthlyInvoiced = new ArrayList<>();
        List<BigDecimal> monthlyPaid = new ArrayList<>();
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM yyyy");

        List<Invoice> allInvoices = invoiceRepository.findAll();

        // 6 months of historical data
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.from(now.minusMonths(i));
            monthlyLabels.add(ym.format(monthFmt));

            BigDecimal sumInvoiced = BigDecimal.ZERO;
            BigDecimal sumPaid = BigDecimal.ZERO;

            for (Invoice inv : allInvoices) {
                if (inv.getIssueDate() != null && YearMonth.from(inv.getIssueDate()).equals(ym)) {
                    BigDecimal amt = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
                    sumInvoiced = sumInvoiced.add(amt);
                    if (inv.getStatus() == InvoiceStatus.PAID) {
                        sumPaid = sumPaid.add(amt);
                    }
                }
            }

            monthlyInvoiced.add(sumInvoiced);
            monthlyPaid.add(sumPaid);
        }

        // 3 years of historical annual data
        List<String> yearlyLabels = new ArrayList<>();
        List<BigDecimal> yearlyInvoiced = new ArrayList<>();
        List<BigDecimal> yearlyPaid = new ArrayList<>();
        int currentYear = now.getYear();

        for (int y = currentYear - 2; y <= currentYear; y++) {
            final int yearVal = y;
            yearlyLabels.add(String.valueOf(yearVal));

            BigDecimal sumYInvoiced = BigDecimal.ZERO;
            BigDecimal sumYPaid = BigDecimal.ZERO;

            for (Invoice inv : allInvoices) {
                if (inv.getIssueDate() != null && inv.getIssueDate().getYear() == yearVal) {
                    BigDecimal amt = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
                    sumYInvoiced = sumYInvoiced.add(amt);
                    if (inv.getStatus() == InvoiceStatus.PAID) {
                        sumYPaid = sumYPaid.add(amt);
                    }
                }
            }

            yearlyInvoiced.add(sumYInvoiced);
            yearlyPaid.add(sumYPaid);
        }

        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        BigDecimal overdueAmount = BigDecimal.ZERO;

        for (Invoice inv : allInvoices) {
            BigDecimal amt = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
            if (inv.getStatus() == InvoiceStatus.PAID) {
                paidAmount = paidAmount.add(amt);
            } else if (inv.getDueDate() != null && inv.getDueDate().isBefore(now)) {
                overdueAmount = overdueAmount.add(amt);
            } else {
                pendingAmount = pendingAmount.add(amt);
            }
        }

        List<String> statusLabels = List.of("Paid", "Pending", "Overdue");
        List<BigDecimal> statusAmounts = List.of(paidAmount, pendingAmount, overdueAmount);
        List<String> statusColors = List.of("#16A34A", "#D97706", "#DC2626");

        boolean hasData = !allInvoices.isEmpty();

        return DashboardChartsDTO.builder()
                .monthlyLabels(monthlyLabels)
                .monthlyInvoiced(monthlyInvoiced)
                .monthlyPaid(monthlyPaid)
                .yearlyLabels(yearlyLabels)
                .yearlyInvoiced(yearlyInvoiced)
                .yearlyPaid(yearlyPaid)
                .statusLabels(statusLabels)
                .statusAmounts(statusAmounts)
                .statusColors(statusColors)
                .hasData(hasData)
                .build();
    }

    private Double computeChangePercent(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            if (current != null && current.compareTo(BigDecimal.ZERO) > 0) {
                return 100.0;
            }
            return 0.0;
        }
        if (current == null) current = BigDecimal.ZERO;

        BigDecimal diff = current.subtract(previous);
        BigDecimal pct = diff.multiply(BigDecimal.valueOf(100)).divide(previous, 1, RoundingMode.HALF_UP);
        return pct.doubleValue();
    }
}
