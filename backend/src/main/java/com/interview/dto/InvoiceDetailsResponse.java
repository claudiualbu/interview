package com.interview.dto;

import com.interview.entity.InvoiceStatus;

import java.time.Instant;
import java.util.List;

public record InvoiceDetailsResponse(
        Long id,
        Long version,
        Long repairOrderId,
        String invoiceNumber,
        InvoiceStatus status,
        int lineItemCount,
        long totalCents,
        List<InvoiceLineItemResponse> lineItems,
        Instant createdAt,
        Instant updatedAt
) {
}
