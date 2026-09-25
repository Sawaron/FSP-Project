package com.codeandpray.result.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PublishResultRequest(
        @NotNull @PositiveOrZero
        Long version
) {
}