package com.example.petshop.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderReference,
        String customerName,
        String storeCode,
        String loyaltySeason,
        BigDecimal amountPerPet,
        int rewardPoints,
        BreakdownResponse breakdown,
        OffsetDateTime createdAt
) {
}
