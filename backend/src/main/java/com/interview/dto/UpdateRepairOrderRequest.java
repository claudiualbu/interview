package com.interview.dto;

import com.interview.entity.RepairOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRepairOrderRequest(
        @NotNull Long version,
        @NotBlank @Size(max = 120) String customerName,
        @NotBlank @Size(max = 40) String vehicleVin,
        @NotNull RepairOrderStatus status
) {
}
