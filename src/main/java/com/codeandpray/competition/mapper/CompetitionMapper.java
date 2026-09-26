package com.codeandpray.competition.mapper;

import com.codeandpray.competition.dto.CompetitionResponse;
import com.codeandpray.competition.entity.Competition;

import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class CompetitionMapper {

    public CompetitionResponse toResponse(Competition c, Instant now) {
        return new CompetitionResponse(c.getId(), c.getVersion(), c.getTitle(), c.getLevel(),
                c.getDisciplineId(), c.getStartsAt(), c.getEndsAt(), c.getFormat(), c.getVenue(),
                c.getDescription(), c.getStatus(), c.getRegistrationOpensAt(), c.getRegistrationClosesAt(),
                c.isRegistrationOpen(now), c.getRules(), c.isContestEnabled(), c.getFinalizedAt());
    }
}
