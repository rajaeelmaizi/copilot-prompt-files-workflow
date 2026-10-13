package com.example.petshop.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The second year of {@code loyaltySeason} must be the first year plus one (e.g. 2026-2027).
 */
@Documented
@Constraint(validatedBy = ConsecutiveSeasonValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ConsecutiveSeason {

    String message() default "The second year must be the first year plus one";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
