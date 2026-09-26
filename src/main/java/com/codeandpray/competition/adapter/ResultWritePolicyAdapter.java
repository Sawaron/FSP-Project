package com.codeandpray.competition.adapter;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.repository.CompetitionRepository;
import com.codeandpray.result.port.ResultWritePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResultWritePolicyAdapter implements ResultWritePolicy {
    private final CompetitionRepository competitions;
    public void requireManual(long competitionId) {
        if (competitions.findById(competitionId).orElseThrow().isContestEnabled()) {
            throw BusinessException.conflict("Результаты контеста формируются только при подведении итогов");
        }
    }
}

