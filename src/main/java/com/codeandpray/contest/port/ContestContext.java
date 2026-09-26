package com.codeandpray.contest.port;

import com.codeandpray.competition.enums.CompetitionStatus;
import java.time.Instant;
import java.util.List;

public interface ContestContext {
    CompetitionView get(long competitionId);
    CompetitionView lock(long competitionId);
    Participant participant(long competitionId, long userId);
    List<Participant> participants(long competitionId);
    void markFinalized(long competitionId, Instant now);

    record CompetitionView(long id, long ownerId, CompetitionStatus status,
                           Instant startsAt, Instant endsAt, boolean enabled, Instant finalizedAt) {}
    record Participant(long registrationId, long athleteId, String fullName) {}
}

