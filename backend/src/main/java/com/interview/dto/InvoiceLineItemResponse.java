package com.interview.dto;

import java.time.Instant;

public record InvoiceLineItemResponse(
        Long id,
        Long version,
        Long invoiceId,
        String description,
        int quantity,
        int unitPriceCents,
        long lineTotalCents,
        Instant createdAt,
        Instant updatedAt
) {
}
