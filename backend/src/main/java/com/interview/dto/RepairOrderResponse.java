package com.interview.dto;

import com.interview.entity.RepairOrderStatus;

import java.time.Instant;

public record RepairOrderResponse(
        Long id,
        String customerName,
        String vehicleVin,
        RepairOrderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
