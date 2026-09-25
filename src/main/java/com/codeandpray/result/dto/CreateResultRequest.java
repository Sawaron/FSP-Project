package com.codeandpray.result.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateResultRequest(
        @NotNull @Positive
        Long registrationId,

        @Positive
        Integer place,

        @Digits(integer = 15, fraction = 4)
        BigDecimal performanceValue,

        @Size(max = 30)
        String performanceUnit
) {
}