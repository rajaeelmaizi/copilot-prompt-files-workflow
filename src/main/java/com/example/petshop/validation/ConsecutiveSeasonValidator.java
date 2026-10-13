package com.example.petshop.validation;

import com.example.petshop.dto.OrderRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConsecutiveSeasonValidator implements ConstraintValidator<ConsecutiveSeason, OrderRequest> {

    private static final String SEASON_PATTERN = "^[0-9]{4}-[0-9]{4}$";

    @Override
    public boolean isValid(OrderRequest request, ConstraintValidatorContext context) {
        if (request == null || request.loyaltySeason() == null
                || !request.loyaltySeason().matches(SEASON_PATTERN)) {
            // Null and format errors are reported by @NotBlank and @Pattern on the field.
            return true;
        }
        int firstYear = Integer.parseInt(request.loyaltySeason().substring(0, 4));
        int secondYear = Integer.parseInt(request.loyaltySeason().substring(5, 9));
        return secondYear == firstYear + 1;
    }
}
