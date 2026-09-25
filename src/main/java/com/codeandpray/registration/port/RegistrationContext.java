package com.codeandpray.registration.port;

import com.codeandpray.competition.enums.CompetitionStatus;
import java.time.Instant;

public interface RegistrationContext {
    long athleteIdForUser(long userId);
    CompetitionView getCompetition(long competitionId);
    CompetitionView lockCompetition(long competitionId);
    void lockAthlete(long athleteId);

    record CompetitionView(
            long id, long organizerId, CompetitionStatus status,
            Instant opensAt, Instant closesAt
    ) {
        public boolean registrationOpen(Instant now) {
            return status == CompetitionStatus.UPCOMING
                    && !now.isBefore(opensAt) && now.isBefore(closesAt);
        }
    }
}