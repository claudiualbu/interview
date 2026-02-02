package com.interview.dto;

import com.interview.entity.InvoiceStatus;

import java.time.Instant;

public record InvoiceResponse(
        Long id,
        Long version,
        Long repairOrderId,
        String invoiceNumber,
        InvoiceStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
