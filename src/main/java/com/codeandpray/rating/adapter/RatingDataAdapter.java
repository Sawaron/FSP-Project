package com.codeandpray.rating.adapter;

import com.codeandpray.rating.port.RatingDataPort;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RatingDataAdapter implements RatingDataPort {

    @Override
    public RatingInput lockAthleteAndRead(long athleteId) {
        return new RatingInput(0L, Collections.emptyList());
    }
}