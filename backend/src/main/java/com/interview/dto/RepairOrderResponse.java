package com.interview.dto;

import com.interview.entity.RepairOrderStatus;

import java.time.Instant;

public record RepairOrderResponse(
        Long id,
        Long version,
        String customerName,
        String vehicleVin,
        RepairOrderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
