package com.codeandpray.contest.dto;

import jakarta.validation.constraints.*;

public record TaskRequest(@NotBlank @Size(max = 200) String title,
                          @NotBlank @Size(max = 20000) String statement,
                          @NotNull @Min(1) @Max(1000000) Integer maxPoints,
                          @NotNull @PositiveOrZero Integer sortOrder) {}

