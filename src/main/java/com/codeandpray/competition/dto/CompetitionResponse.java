package com.codeandpray.competition.dto;

import com.codeandpray.competition.enums.*;

import java.time.Instant;

public record CompetitionResponse(long id, long version, String title, CompetitionLevel level,
                                  long disciplineId, Instant startsAt, Instant endsAt, CompetitionFormat format,
                                  String venue, String description, CompetitionStatus status,
                                  Instant registrationOpensAt, Instant registrationClosesAt, boolean registrationOpen,
                                  String rules, boolean contestEnabled, Instant finalizedAt) {
}
