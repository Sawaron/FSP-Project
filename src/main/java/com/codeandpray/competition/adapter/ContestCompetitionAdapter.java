package com.codeandpray.competition.adapter;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.entity.Competition;
import com.codeandpray.competition.repository.CompetitionRepository;
import com.codeandpray.contest.port.ContestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContestCompetitionAdapter implements ContestContext {
    private final CompetitionRepository competitions;
    private final JdbcTemplate jdbc;

    public CompetitionView get(long id) {
        return view(competitions.findById(id).orElseThrow(() -> BusinessException.notFound("Соревнование не найдено")));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public CompetitionView lock(long id) {
        return view(competitions.findForUpdate(id).orElseThrow(() -> BusinessException.notFound("Соревнование не найдено")));
    }

    public Participant participant(long competitionId, long userId) {
        return jdbc.query("""
                select r.id, a.id as athlete_id, a.full_name
                from registrations r join athlete_profiles a on a.id = r.athlete_id
                where r.competition_id = ? and a.user_id = ? and r.status = 'REGISTERED'
                """, (rs, n) -> new Participant(rs.getLong("id"), rs.getLong("athlete_id"),
                rs.getString("full_name")), competitionId, userId).stream().findFirst()
                .orElseThrow(() -> BusinessException.forbidden("Требуется действующая заявка на соревнование"));
    }

    public List<Participant> participants(long competitionId) {
        return jdbc.query("""
                select r.id, a.id as athlete_id, a.full_name
                from registrations r join athlete_profiles a on a.id = r.athlete_id
                where r.competition_id = ? and r.status = 'REGISTERED'
                order by a.id
                """, (rs, n) -> new Participant(rs.getLong("id"), rs.getLong("athlete_id"),
                rs.getString("full_name")), competitionId);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void markFinalized(long competitionId, Instant now) {
        Competition c = competitions.findForUpdate(competitionId).orElseThrow();
        c.finalizeResults(now);
        competitions.flush();
    }

    private CompetitionView view(Competition c) {
        return new CompetitionView(c.getId(), c.getCreatedByUserId(), c.getStatus(),
                c.getStartsAt(), c.getEndsAt(), c.isContestEnabled(), c.getFinalizedAt());
    }
}

