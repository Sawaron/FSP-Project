package com.codeandpray.rating.service;

import com.codeandpray.athlete.port.AthleteRating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AthleteRatingAdapter implements AthleteRating {

    private final RatingService ratingService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void recalculate(long athleteId) {
        ratingService.recalculate(athleteId);
    }
}