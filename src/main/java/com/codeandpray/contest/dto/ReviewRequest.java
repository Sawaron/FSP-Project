package com.codeandpray.contest.dto;

import jakarta.validation.constraints.*;

public record ReviewRequest(@NotNull @PositiveOrZero Long version,
                            @NotNull @PositiveOrZero Integer points,
                            @Size(max = 5000) String comment) {}

