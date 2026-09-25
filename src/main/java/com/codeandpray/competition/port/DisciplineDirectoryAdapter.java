//package com.codeandpray.competition.port;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class DisciplineDirectoryAdapter implements DisciplineDirectory {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Override
//    public boolean exists(long disciplineId) {
//        Integer count = jdbcTemplate.queryForObject(
//                "SELECT COUNT(*) FROM disciplines WHERE id = ?",
//                Integer.class,
//                disciplineId
//        );
//        return count != null && count > 0;
//    }
//}
//TODO раскоментировать после слияеня