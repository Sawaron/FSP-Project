package com.codeandpray.result.port;

import com.codeandpray.competition.enums.CompetitionLevel;

public interface ResultRating {

    Score calculate(Integer place, CompetitionLevel level);

    void recalculate(long athleteId);

    record Score(long points, int formulaVersion) {
        public Score {
            if (points < 0 || formulaVersion <= 0) {
                throw new IllegalArgumentException(
                        "Некорректный расчёт рейтинга"
                );
            }
        }
    }
}