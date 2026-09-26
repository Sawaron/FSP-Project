package com.codeandpray.contest.adapter;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.port.CompetitionTasks;
import com.codeandpray.contest.repository.ContestTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompetitionTasksAdapter implements CompetitionTasks {
    private final ContestTaskRepository tasks;
    public void requireReady(long competitionId) {
        if (!hasTasks(competitionId)) throw BusinessException.conflict("Добавьте хотя бы одно задание перед публикацией");
    }
    public boolean hasTasks(long competitionId) {
        return tasks.existsByCompetitionId(competitionId);
    }
}

