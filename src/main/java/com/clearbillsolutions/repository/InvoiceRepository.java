package com.clearbillsolutions.repository;

import com.clearbillsolutions.entity.Invoice;
import com.clearbillsolutions.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findAllByOrderByCreatedAtDesc();

    List<Invoice> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Invoice> findByStatusOrderByCreatedAtDesc(InvoiceStatus status);

    List<Invoice> findTop5ByOrderByCreatedAtDesc();

    long countByStatus(InvoiceStatus status);

    boolean existsByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findTopByOrderByIdDesc();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.00) FROM Invoice i WHERE i.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") InvoiceStatus status);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.00) FROM Invoice i")
    BigDecimal sumTotalAmount();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.dueDate < :today AND i.status <> 'PAID'")
    long countOverdueInvoices(@Param("today") LocalDate today);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.00) FROM Invoice i WHERE i.status <> 'PAID'")
    BigDecimal sumUnpaidAmount();
}
