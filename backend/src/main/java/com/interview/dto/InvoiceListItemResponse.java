package com.interview.dto;

import com.interview.entity.InvoiceStatus;

public record InvoiceListItemResponse(
        Long id,
        Long repairOrderId,
        String invoiceNumber,
        InvoiceStatus status,
        int lineItemCount,
        long totalCents
) {
}
