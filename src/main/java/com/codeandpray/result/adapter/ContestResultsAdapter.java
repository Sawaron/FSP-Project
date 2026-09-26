package com.codeandpray.result.adapter;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.security.CurrentActor;
import com.codeandpray.competition.enums.CompetitionStatus;
import com.codeandpray.contest.port.ContestResults;
import com.codeandpray.result.entity.Result;
import com.codeandpray.result.port.ResultParticipation;
import com.codeandpray.result.port.ResultRating;
import com.codeandpray.result.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContestResultsAdapter implements ContestResults {
    private final ResultRepository results;
    private final ResultParticipation participation;
    private final ResultRating rating;
    private final CurrentActor actor;
    private final Clock clock;
    private final JdbcTemplate jdbc;

    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(long competitionId, List<Standing> standings) {
        long organizer = actor.requireOrganizerId();
        for (Standing row : standings.stream().sorted(Comparator.comparingLong(Standing::athleteId)).toList()) {
            var p = participation.lockForResultWrite(row.registrationId());
            if (p.competitionId() != competitionId || p.athleteId() != row.athleteId()
                    || p.organizerId() != organizer || !p.active() || p.competitionStatus() != CompetitionStatus.COMPLETED) {
                throw BusinessException.conflict("Изменились данные участника");
            }
            if (results.existsByRegistrationId(row.registrationId())) {
                throw BusinessException.conflict("Для участника уже существует результат");
            }
            Result result = Result.createDraft(row.registrationId(), organizer, row.place(),
                    BigDecimal.valueOf(row.points()), "POINTS", clock.instant());
            var score = rating.calculate(row.place(), p.competitionLevel());
            result.publish(score.points(), score.formulaVersion(), clock.instant());
            results.saveAndFlush(result);
            rating.recalculate(row.athleteId());
        }
    }

    public List<Standing> published(long competitionId) {
        return jdbc.query("""
                select r.registration_id, a.id as athlete_id, a.full_name, r.place, r.performance_value
                from results r join registrations reg on reg.id = r.registration_id
                join athlete_profiles a on a.id = reg.athlete_id
                where reg.competition_id = ? and r.status = 'PUBLISHED'
                order by r.place, a.id
                """, (rs, n) -> new Standing(rs.getLong("registration_id"), rs.getLong("athlete_id"),
                rs.getString("full_name"), rs.getInt("place"), rs.getBigDecimal("performance_value").longValueExact()), competitionId);
    }
}

