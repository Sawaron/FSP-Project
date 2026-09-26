package com.codeandpray.competition.port;

public interface CompetitionTasks {
    void requireReady(long competitionId);
    boolean hasTasks(long competitionId);
}

