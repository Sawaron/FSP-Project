package com.codeandpray.rating.port;

import java.util.List;

public interface RatingDataPort {
    RatingInput lockAthleteAndRead(long athleteId);

    record PublishedResult(long resultId, long ratingPoints, int formulaVersion) {
    }

    record RatingInput(long qualificationBonus, List<PublishedResult> results) {
        public RatingInput {
            results = List.copyOf(results);
        }
    }
}