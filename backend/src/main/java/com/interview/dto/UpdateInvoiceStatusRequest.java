package com.interview.dto;

import com.interview.entity.InvoiceStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateInvoiceStatusRequest(
        @NotNull Long version,
        @NotNull InvoiceStatus status
) {}
