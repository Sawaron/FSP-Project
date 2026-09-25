package com.codeandpray.rating.port;

import com.codeandpray.competition.enums.CompetitionLevel;

import java.util.List;


public interface RatingDataPort {
    RatingInput lockAthleteAndRead(long athleteId);

    record PublishedResult(long resultId, Integer place, CompetitionLevel level) {
    }

    record RatingInput(long qualificationBonus, List<PublishedResult> results) {
        public RatingInput {
            results = List.copyOf(results);
        }
    }
}
