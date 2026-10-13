package com.example.petshop.dto;

public record BreakdownResponse(
        int loyalty,
        int pickup,
        int basket,
        int adoption,
        int subscription
) {
}
