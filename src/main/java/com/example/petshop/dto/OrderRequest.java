package com.example.petshop.dto;

import com.example.petshop.model.PickupMethod;
import com.example.petshop.validation.ConsecutiveSeason;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@ConsecutiveSeason
public record OrderRequest(
        @NotBlank
        @Pattern(regexp = "^[0-9]{8}$")
        String orderReference,
        @NotBlank
        @Size(min = 2, max = 80)
        String customerName,
        @NotBlank
        @Pattern(regexp = "^[0-9]{8}$")
        String storeCode,
        @NotBlank
        @Pattern(regexp = "^[0-9]{4}-[0-9]{4}$")
        String loyaltySeason,
        @NotNull
        Boolean loyaltyMember,
        @NotNull
        PickupMethod pickupMethod,
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Digits(integer = 100, fraction = 2)
        BigDecimal orderTotal,
        @NotNull
        @Min(1)
        @Max(15)
        Integer petCount,
        @NotNull
        Boolean adoptedPet,
        @NotNull
        Boolean subscription
) {
}
