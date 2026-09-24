package com.codeandpray.competition.dto;

import com.codeandpray.competition.enums.CompetitionStatus;
import jakarta.validation.constraints.*;

public record ChangeCompetitionStatusRequest(
        @NotNull
        CompetitionStatus status,
        @NotNull @PositiveOrZero
        Long version) {
}
