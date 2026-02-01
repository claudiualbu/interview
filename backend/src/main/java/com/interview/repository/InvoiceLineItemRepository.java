package com.interview.repository;

import com.interview.entity.InvoiceLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, Long> {
    List<InvoiceLineItem> findAllByInvoiceIdOrderByIdAsc(Long invoiceId);
}
