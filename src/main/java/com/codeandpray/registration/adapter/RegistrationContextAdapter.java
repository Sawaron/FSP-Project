package com.codeandpray.registration.adapter;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.enums.CompetitionStatus;
import com.codeandpray.registration.port.RegistrationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrationContextAdapter implements RegistrationContext {
    private final JdbcTemplate jdbc;

    @Override
    public long athleteIdForUser(long userId) {
        return jdbc.query("select id from athlete_profiles where user_id = ?",
                        (rs, row) -> rs.getLong("id"), userId).stream().findFirst()
                .orElseThrow(() -> BusinessException.badRequest("Сначала заполните профиль спортсмена"));
    }

    @Override
    public CompetitionView getCompetition(long id) {
        return readCompetition(id, "");
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CompetitionView lockCompetition(long id) {
        return readCompetition(id, " for update");
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void lockAthlete(long athleteId) {
        var ids = jdbc.query("select id from athlete_profiles where id = ? for update",
                (rs, row) -> rs.getLong("id"), athleteId);
        if (ids.isEmpty()) {
            throw BusinessException.notFound("Спортсмен не найден");
        }
    }

    private CompetitionView readCompetition(long id, String suffix) {
        return jdbc.query("""
                select id, created_by_user_id, status,
                       registration_opens_at, registration_closes_at
                from competitions where id = ?
                """ + suffix, (rs, row) -> new CompetitionView(
                        rs.getLong("id"), rs.getLong("created_by_user_id"),
                        CompetitionStatus.valueOf(rs.getString("status")),
                        rs.getTimestamp("registration_opens_at").toInstant(),
                        rs.getTimestamp("registration_closes_at").toInstant()), id)
                .stream().findFirst()
                .orElseThrow(() -> BusinessException.notFound("Соревнование не найдено"));
    }
}