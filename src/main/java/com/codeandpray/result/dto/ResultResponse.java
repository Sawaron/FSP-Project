package com.codeandpray.result.dto;

import com.codeandpray.result.enums.ResultStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ResultResponse(
        long id,
        long version,
        long registrationId,
        Integer place,
        BigDecimal performanceValue,
        String performanceUnit,
        ResultStatus status,
        Long ratingPoints,
        Integer formulaVersion,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt
) {
}