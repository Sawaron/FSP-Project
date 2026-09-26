package com.codeandpray.contest.port;

import java.util.List;

public interface ContestResults {
    void publish(long competitionId, List<Standing> standings);
    List<Standing> published(long competitionId);
    record Standing(long registrationId, long athleteId, String fullName, int place, long points) {}
}

