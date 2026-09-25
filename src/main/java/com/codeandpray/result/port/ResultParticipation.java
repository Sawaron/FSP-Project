package com.codeandpray.result.port;

import com.codeandpray.competition.enums.CompetitionLevel;
import com.codeandpray.competition.enums.CompetitionStatus;

public interface ResultParticipation {
    ParticipationView get(long registrationId);
    ParticipationView lockForResultWrite(long registrationId);

    record ParticipationView(
            long registrationId, long athleteId, long competitionId,
            long organizerId, CompetitionLevel competitionLevel,
            CompetitionStatus competitionStatus, boolean active
    ) {
    }
}