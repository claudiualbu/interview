package com.interview.repository;

import com.interview.entity.InvoiceLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, Long> {
    List<InvoiceLineItem> findAllByInvoiceIdOrderByIdAsc(Long invoiceId);

    @Query("select li from InvoiceLineItem li join fetch li.invoice where li.id = :id")
    Optional<InvoiceLineItem> findByIdWithInvoice(Long id);
}
