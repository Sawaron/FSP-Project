package com.codeandpray.result.service;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.security.CurrentActor;
import com.codeandpray.common.web.PageRequests;
import com.codeandpray.common.web.PageResponse;
import com.codeandpray.competition.enums.CompetitionStatus;
import com.codeandpray.result.dto.CreateResultRequest;
import com.codeandpray.result.dto.PublishResultRequest;
import com.codeandpray.result.dto.ResultResponse;
import com.codeandpray.result.dto.UpdateResultRequest;
import com.codeandpray.result.entity.Result;
import com.codeandpray.result.enums.ResultStatus;
import com.codeandpray.result.mapper.ResultMapper;
import com.codeandpray.result.port.ResultParticipation;
import com.codeandpray.result.port.ResultParticipation.ParticipationView;
import com.codeandpray.result.port.ResultRating;
import com.codeandpray.result.port.ResultAthleteDirectory;
import com.codeandpray.result.repository.ResultRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@Transactional(readOnly = true)
@lombok.RequiredArgsConstructor
public class ResultService {

    private final ResultRepository repository;
    private final ResultParticipation participation;
    private final ResultRating rating;
    private final ResultAthleteDirectory athletes;
    private final CurrentActor actor;
    private final ResultMapper mapper;
    private final Clock clock;

    public ResultResponse getPublished(long id) {
        requirePositiveId(id);

        Result result = repository.findByIdAndStatus(
                id,
                ResultStatus.PUBLISHED
        ).orElseThrow(() -> BusinessException.notFound(
                "Опубликованный результат не найден"
        ));

        return mapper.toResponse(result);
    }

    public PageResponse<ResultResponse> listPublished(int page, int size) {
        var pageable = PageRequests.of(
                page,
                size,
                Sort.by("publishedAt").descending()
                        .and(Sort.by("id").descending())
        );

        return PageResponse.from(
                repository.findByStatus(ResultStatus.PUBLISHED, pageable)
                        .map(mapper::toResponse)
        );
    }

    public PageResponse<ResultResponse> listPublishedByAthlete(
            long athleteId,
            int page,
            int size
    ) {
        requirePositiveId(athleteId);
        if (!athletes.exists(athleteId)) {
            throw BusinessException.notFound("Спортсмен не найден");
        }
        return PageResponse.from(repository.findPublishedByAthleteId(
                athleteId,
                PageRequests.of(page, size, Sort.unsorted())
        ).map(mapper::toResponse));
    }

    public ResultResponse getForOrganizer(long id) {
        requirePositiveId(id);
        long organizerId = actor.requireOrganizerId();

        Result result = repository.findById(id)
                .orElseThrow(() -> BusinessException.notFound(
                        "Результат не найден"
                ));

        ParticipationView context = participation.get(
                result.getRegistrationId()
        );

        requireOwner(context, organizerId);

        return mapper.toResponse(result);
    }

    @Transactional
    public ResultResponse create(CreateResultRequest request) {
        long organizerId = actor.requireOrganizerId();

        ParticipationView context = participation.lockForResultWrite(
                request.registrationId()
        );

        requireOwner(context, organizerId);
        requireActiveRegistration(context);
        requireStartedCompetition(context);

        if (repository.existsByRegistrationId(request.registrationId())) {
            throw BusinessException.conflict(
                    "Для этой заявки результат уже создан"
            );
        }

        Result result = Result.createDraft(
                request.registrationId(),
                organizerId,
                request.place(),
                request.performanceValue(),
                request.performanceUnit(),
                clock.instant()
        );

        return mapper.toResponse(repository.saveAndFlush(result));
    }

    @Transactional
    public ResultResponse updateDraft(
            long id,
            UpdateResultRequest request
    ) {
        LockedResult locked = lockOwnedResult(id);

        requireActiveRegistration(locked.context());
        requireStartedCompetition(locked.context());

        Result result = locked.result();
        result.requireVersion(request.version());

        result.updateDraft(
                request.place(),
                request.performanceValue(),
                request.performanceUnit(),
                clock.instant()
        );

        repository.flush();

        return mapper.toResponse(result);
    }

    @Transactional
    public ResultResponse publish(
            long id,
            PublishResultRequest request
    ) {
        LockedResult locked = lockOwnedResult(id);

        requireActiveRegistration(locked.context());
        requireCompletedCompetition(locked.context());

        Result result = locked.result();

        if (result.getStatus() == ResultStatus.PUBLISHED) {
            return mapper.toResponse(result);
        }

        result.requireVersion(request.version());

        ResultRating.Score score = rating.calculate(
                result.getPlace(),
                locked.context().competitionLevel()
        );

        result.publish(
                score.points(),
                score.formulaVersion(),
                clock.instant()
        );

        repository.flush();

        rating.recalculate(locked.context().athleteId());

        return mapper.toResponse(result);
    }

    @Transactional
    public ResultResponse correctPublished(
            long id,
            UpdateResultRequest request
    ) {
        LockedResult locked = lockOwnedResult(id);

        requireActiveRegistration(locked.context());
        requireCompletedCompetition(locked.context());

        Result result = locked.result();
        result.requireVersion(request.version());

        ResultRating.Score score = rating.calculate(
                request.place(),
                locked.context().competitionLevel()
        );

        result.correctPublished(
                request.place(),
                request.performanceValue(),
                request.performanceUnit(),
                score.points(),
                score.formulaVersion(),
                clock.instant()
        );

        repository.flush();
        rating.recalculate(locked.context().athleteId());

        return mapper.toResponse(result);
    }

    @Transactional
    public void deleteDraft(long id, long version) {
        LockedResult locked = lockOwnedResult(id);

        Result result = locked.result();
        result.requireVersion(version);
        result.requireDraft();

        repository.delete(result);
        repository.flush();
    }

    private LockedResult lockOwnedResult(long id) {
        requirePositiveId(id);
        long organizerId = actor.requireOrganizerId();

        long registrationId = repository.findRegistrationIdById(id)
                .orElseThrow(() -> BusinessException.notFound(
                        "Результат не найден"
                ));

        ParticipationView context =
                participation.lockForResultWrite(registrationId);

        requireOwner(context, organizerId);

        Result result = repository.findForUpdate(id)
                .orElseThrow(() -> BusinessException.notFound(
                        "Результат не найден"
                ));

        return new LockedResult(result, context);
    }

    private static void requireOwner(
            ParticipationView context,
            long organizerId
    ) {
        if (context.organizerId() != organizerId) {
            throw new AccessDeniedException(
                    "Управлять результатами можно только своего соревнования"
            );
        }
    }

    private static void requireActiveRegistration(
            ParticipationView context
    ) {
        if (!context.active()) {
            throw BusinessException.conflict(
                    "Нельзя вносить результат отменённой заявки"
            );
        }
    }

    private static void requireStartedCompetition(
            ParticipationView context
    ) {
        CompetitionStatus status = context.competitionStatus();

        if (status != CompetitionStatus.ONGOING
                && status != CompetitionStatus.COMPLETED) {
            throw BusinessException.conflict(
                    "Результат можно внести после начала соревнования"
            );
        }
    }

    private static void requireCompletedCompetition(
            ParticipationView context
    ) {
        if (context.competitionStatus() != CompetitionStatus.COMPLETED) {
            throw BusinessException.conflict(
                    "Публикация и исправление доступны после завершения соревнования"
            );
        }
    }

    private static void requirePositiveId(long id) {
        if (id <= 0) {
            throw BusinessException.badRequest(
                    "Идентификатор должен быть положительным"
            );
        }
    }

    private record LockedResult(
            Result result,
            ParticipationView context
    ) {
    }
}
