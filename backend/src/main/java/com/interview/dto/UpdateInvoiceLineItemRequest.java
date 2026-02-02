package com.interview.dto;

import jakarta.validation.constraints.*;

public record UpdateInvoiceLineItemRequest(
        @NotNull Long version,
        @NotBlank @Size(max = 200) String description,
        @Positive int quantity,
        @PositiveOrZero int unitPriceCents
) {
}
