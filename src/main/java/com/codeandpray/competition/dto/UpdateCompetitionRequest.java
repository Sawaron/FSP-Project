package com.codeandpray.competition.dto;

import com.codeandpray.competition.entity.CompetitionDetails;
import com.codeandpray.competition.enums.*;
import jakarta.validation.constraints.*;

import java.time.Instant;

public record UpdateCompetitionRequest(
        @NotBlank @Size(max = 200)
        String title,
        @NotNull CompetitionLevel
        level,
        @NotNull @Positive
        Long disciplineId,
        @NotNull
        Instant startsAt,
        @NotNull
        Instant endsAt,
        @NotNull
        CompetitionFormat format,
        @Size(max = 500)
        String venue,
        @NotNull @Size(max = 5000)
        String description,
        @NotNull
        Instant registrationOpensAt,
        @NotNull
        Instant registrationClosesAt,
        @NotNull @PositiveOrZero
        Long version,
        @NotBlank @Size(max = 5000) String rules) {

    public CompetitionDetails toDetails() {
        return new CompetitionDetails(title, level, disciplineId, startsAt, endsAt, format,
                venue, description, registrationOpensAt, registrationClosesAt);
    }
}
