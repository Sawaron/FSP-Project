package com.codeandpray.registration.adapter;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.enums.CompetitionLevel;
import com.codeandpray.competition.enums.CompetitionStatus;
import com.codeandpray.registration.port.RegistrationContext;
import com.codeandpray.result.port.ResultParticipation;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResultParticipationAdapter implements ResultParticipation {
    private final JdbcTemplate jdbc;
    private final RegistrationContext context;

    @Override
    public ParticipationView get(long id) {
        return jdbc.query("""
                select r.id, r.athlete_id, r.competition_id, r.status as registration_status,
                       c.created_by_user_id, c.level, c.status as competition_status
                from registrations r join competitions c on c.id = r.competition_id
                where r.id = ?
                """, (rs, row) -> new ParticipationView(
                        rs.getLong("id"), rs.getLong("athlete_id"), rs.getLong("competition_id"),
                        rs.getLong("created_by_user_id"),
                        CompetitionLevel.valueOf(rs.getString("level")),
                        CompetitionStatus.valueOf(rs.getString("competition_status")),
                        "REGISTERED".equals(rs.getString("registration_status"))), id)
                .stream().findFirst()
                .orElseThrow(() -> BusinessException.notFound("Заявка не найдена"));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public ParticipationView lockForResultWrite(long id) {
        ParticipationView initial = get(id);
        context.lockCompetition(initial.competitionId());
        context.lockAthlete(initial.athleteId());
        var ids = jdbc.query("select id from registrations where id = ? for update",
                (rs, row) -> rs.getLong("id"), id);
        if (ids.isEmpty()) {
            throw BusinessException.notFound("Заявка не найдена");
        }
        ParticipationView current = get(id);
        if (current.competitionId() != initial.competitionId()
                || current.athleteId() != initial.athleteId()) {
            throw BusinessException.conflict("Изменились связи заявки");
        }
        return current;
    }
}