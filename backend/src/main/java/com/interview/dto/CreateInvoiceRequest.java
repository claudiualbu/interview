package com.interview.dto;

import jakarta.validation.constraints.NotNull;

public record CreateInvoiceRequest(
        @NotNull Long repairOrderId
) {
}
