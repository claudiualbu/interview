package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRepairOrderRequest(
        @NotBlank @Size(max = 120) String customerName,
        @NotBlank @Size(max = 40) String vehicleVin
) {
}
