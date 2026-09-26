package com.codeandpray.contest.repository;

import com.codeandpray.contest.entity.ContestTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContestTaskRepository extends JpaRepository<ContestTask, Long> {
    List<ContestTask> findByCompetitionIdOrderBySortOrderAscIdAsc(long competitionId);
    long countByCompetitionId(long competitionId);
    boolean existsByCompetitionId(long competitionId);
}

