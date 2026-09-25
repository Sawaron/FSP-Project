package com.codeandpray.rating.service;

import com.codeandpray.competition.enums.CompetitionLevel;
import com.codeandpray.result.port.ResultRating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResultRatingAdapter implements ResultRating {
    private final RatingCalculator calculator;
    private final RatingService service;

    @Override
    public Score calculate(Integer place, CompetitionLevel level) {
        return new Score(calculator.pointsFor(place, level), RatingCalculator.FORMULA_VERSION);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void recalculate(long athleteId) {
        service.recalculate(athleteId);
    }
}