package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateInvoiceLineItemRequest(
        @NotBlank @Size(max = 200) String description,
        @Positive int quantity,
        @PositiveOrZero int unitPriceCents
) {
}
