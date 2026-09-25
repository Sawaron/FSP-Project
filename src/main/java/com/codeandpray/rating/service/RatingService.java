package com.codeandpray.rating.service;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.web.*;
import com.codeandpray.rating.dto.RatingResponse;
import com.codeandpray.rating.entity.RatingSnapshot;
import com.codeandpray.rating.mapper.RatingMapper;
import com.codeandpray.rating.port.RatingDataPort;
import com.codeandpray.rating.repository.RatingSnapshotRepository;

import java.time.Clock;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
@Transactional(readOnly = true)
@lombok.RequiredArgsConstructor
public class RatingService {
    private final RatingSnapshotRepository repository;
    private final RatingDataPort data;
    private final RatingCalculator calculator;
    private final RatingMapper mapper;
    private final Clock clock;


    @Transactional(propagation = Propagation.MANDATORY)
    public RatingResponse recalculate(long athleteId) {
        if (athleteId <= 0) throw BusinessException.badRequest("Некорректный спортсмен");
        var input = data.lockAthleteAndRead(athleteId);
        var result = calculator.calculate(input);
        var snapshot = RatingSnapshot.create(athleteId, result.resultPoints(), result.qualificationPoints(),
                clock.instant(), RatingCalculator.FORMULA_VERSION);
        return mapper.toResponse(repository.saveAndFlush(snapshot));
    }

    public RatingResponse current(long athleteId) {
        return mapper.toResponse(repository.findFirstByAthleteIdOrderByIdDesc(athleteId)
                .orElseThrow(() -> BusinessException.notFound("Рейтинг спортсмена ещё не рассчитан")));
    }

    public PageResponse<RatingResponse> history(long athleteId, int page, int size) {
        return PageResponse.from(repository.findByAthleteId(athleteId,
                PageRequests.of(page, size, Sort.by("id").descending())).map(mapper::toResponse));
    }

    public PageResponse<RatingResponse> leaderboard(int page, int size) {
        return PageResponse.from(repository.findLatestForEachAthlete(PageRequests.of(page, size,
                Sort.by("totalPoints").descending().and(Sort.by("athleteId")))).map(mapper::toResponse));
    }
}