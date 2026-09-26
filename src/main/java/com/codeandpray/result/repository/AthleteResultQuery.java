package com.codeandpray.result.repository;

import com.codeandpray.result.dto.AthleteResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AthleteResultQuery {
    private final JdbcTemplate jdbc;

    public Page<AthleteResultResponse> find(long athleteId, Pageable page) {
        Long count = jdbc.queryForObject("""
                select count(*) from results r join registrations reg on reg.id = r.registration_id
                where reg.athlete_id = ? and r.status = 'PUBLISHED'
                """, Long.class, athleteId);
        var rows = jdbc.query("""
                select r.*, c.id as competition_id, c.title, c.starts_at
                from results r join registrations reg on reg.id = r.registration_id
                join competitions c on c.id = reg.competition_id
                where reg.athlete_id = ? and r.status = 'PUBLISHED'
                order by r.published_at desc, r.id desc limit ? offset ?
                """, (rs, n) -> new AthleteResultResponse(rs.getLong("id"), rs.getLong("registration_id"),
                rs.getLong("competition_id"), rs.getString("title"), rs.getTimestamp("starts_at").toInstant(),
                rs.getObject("place", Integer.class), rs.getBigDecimal("performance_value"),
                rs.getString("performance_unit"), rs.getLong("rating_points"),
                rs.getTimestamp("published_at").toInstant(), rs.getString("status")),
                athleteId, page.getPageSize(), page.getOffset());
        return new PageImpl<>(rows, page, count);
    }
}
