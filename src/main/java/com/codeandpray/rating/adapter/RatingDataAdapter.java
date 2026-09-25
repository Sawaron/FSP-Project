package com.codeandpray.rating.adapter;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.rating.port.RatingDataPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RatingDataAdapter implements RatingDataPort {
    private final JdbcTemplate jdbc;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public RatingInput lockAthleteAndRead(long athleteId) {
        var ids = jdbc.query("select id from athlete_profiles where id = ? for update",
                (rs, row) -> rs.getLong("id"), athleteId);
        if (ids.isEmpty()) {
            throw BusinessException.notFound("Спортсмен не найден");
        }
        Long bonus = jdbc.queryForObject("""
                select coalesce(q.rating_bonus, 0)
                from athlete_profiles a
                left join qualifications q on q.id = a.qualification_id
                where a.id = ?
                """, Long.class, athleteId);
        var results = jdbc.query("""
                select r.id, r.rating_points, r.formula_version
                from results r
                join registrations reg on reg.id = r.registration_id
                where reg.athlete_id = ? and r.status = 'PUBLISHED'
                order by r.id
                """, (rs, row) -> {
            Long points = rs.getObject("rating_points", Long.class);
            Integer version = rs.getObject("formula_version", Integer.class);
            if (points == null || version == null) {
                throw new IllegalStateException("Published result has no rating metadata");
            }
            return new PublishedResult(rs.getLong("id"), points, version);
        }, athleteId);
        return new RatingInput(bonus, results);
    }
}