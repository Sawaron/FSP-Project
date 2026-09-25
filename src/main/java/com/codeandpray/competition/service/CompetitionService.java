package com.codeandpray.competition.service;
import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.security.CurrentActor;
import com.codeandpray.common.web.*;
import com.codeandpray.competition.dto.*;
import com.codeandpray.competition.entity.Competition;
import com.codeandpray.competition.enums.CompetitionStatus;
import com.codeandpray.competition.mapper.CompetitionMapper;
import com.codeandpray.competition.port.*;
import com.codeandpray.competition.repository.CompetitionRepository;
import java.time.Clock;
import java.time.Instant;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@lombok.RequiredArgsConstructor
public class CompetitionService {
    private final CompetitionRepository repository;
    private final DisciplineDirectory disciplines;
    private final CompetitionParticipation participation;
    private final CurrentActor actor;
    private final CompetitionMapper mapper;
    private final Clock clock;

    public CompetitionResponse get(long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> BusinessException.notFound("Соревнование не найдено")), clock.instant());
    }
    public PageResponse<CompetitionResponse> list(CompetitionStatus status, Long disciplineId, int page, int size) {
        if (disciplineId != null && disciplineId <= 0) throw BusinessException.badRequest("Некорректная дисциплина");
        Instant now = clock.instant();
        return PageResponse.from(repository.search(status, disciplineId,
                        PageRequests.of(page, size, Sort.by("startsAt").descending().and(Sort.by("id").descending())))
                .map(c -> mapper.toResponse(c, now)));
    }
    @Transactional
    public CompetitionResponse create(CreateCompetitionRequest request) {
        long ownerId = actor.requireOrganizerId();
        requireDiscipline(request.disciplineId());
        Competition c = Competition.create(request.toDetails(), ownerId, clock.instant());
        return mapper.toResponse(repository.saveAndFlush(c), clock.instant());
    }
    @Transactional
    public CompetitionResponse update(long id, UpdateCompetitionRequest request) {
        Competition c = ownedForUpdate(id, request.version());
        requireDiscipline(request.disciplineId());
        c.updateDetails(request.toDetails(), participation.hasAnyRegistration(id), clock.instant());
        repository.flush();
        return mapper.toResponse(c, clock.instant());
    }
    @Transactional
    public CompetitionResponse changeStatus(long id, ChangeCompetitionStatusRequest request) {
        Competition c = ownedForUpdate(id, request.version());
        c.changeStatus(request.status(), clock.instant());
        repository.flush();
        return mapper.toResponse(c, clock.instant());
    }
    @Transactional
    public void delete(long id, long version) {
        Competition c = ownedForUpdate(id, version);
        if (participation.hasAnyRegistration(id)) {
            throw BusinessException.conflict("Есть заявки: удаление запрещено; используйте отмену, если она допустима");
        }
        repository.delete(c);
        repository.flush();
    }
    private Competition ownedForUpdate(long id, long version) {
        long ownerId = actor.requireOrganizerId();
        Competition c = repository.findForUpdate(id).orElseThrow(() -> BusinessException.notFound("Соревнование не найдено"));
        c.requireOwner(ownerId);
        c.requireVersion(version);
        return c;
    }
    private void requireDiscipline(long id) {
        if (!disciplines.exists(id)) throw BusinessException.badRequest("Дисциплина не найдена");
    }
}