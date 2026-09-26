package com.codeandpray.contest.service;

import com.codeandpray.auth.enums.UserRole;
import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.security.CurrentActor;
import com.codeandpray.common.web.PageRequests;
import com.codeandpray.common.web.PageResponse;
import com.codeandpray.competition.enums.CompetitionStatus;
import com.codeandpray.contest.dto.*;
import com.codeandpray.contest.entity.*;
import com.codeandpray.contest.port.ContestContext;
import com.codeandpray.contest.port.ContestContext.CompetitionView;
import com.codeandpray.contest.port.ContestResults;
import com.codeandpray.contest.port.ContestResults.Standing;
import com.codeandpray.contest.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContestService {
    private final ContestContext context;
    private final ContestResults results;
    private final ContestTaskRepository tasks;
    private final SubmissionRepository submissions;
    private final CurrentActor actor;
    private final Clock clock;

    public List<TaskResponse> tasks(long competitionId, boolean management) {
        CompetitionView c = context.get(competitionId);
        if (management) requireOwner(c); else requireParticipantAccess(c);
        return tasks.findByCompetitionIdOrderBySortOrderAscIdAsc(competitionId).stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse createTask(long competitionId, TaskRequest request) {
        CompetitionView c = context.lock(competitionId);
        requireEditable(c);
        if (tasks.countByCompetitionId(competitionId) >= 100) {
            throw BusinessException.conflict("Допускается до 100 заданий");
        }
        return TaskResponse.from(tasks.saveAndFlush(ContestTask.create(competitionId, request.title(),
                request.statement(), request.maxPoints(), request.sortOrder())));
    }

    @Transactional
    public TaskResponse updateTask(long competitionId, long taskId, long version, TaskRequest request) {
        requireEditable(context.lock(competitionId));
        ContestTask task = task(competitionId, taskId);
        task.requireVersion(version);
        task.change(request.title(), request.statement(), request.maxPoints(), request.sortOrder());
        tasks.flush();
        return TaskResponse.from(task);
    }

    @Transactional
    public void deleteTask(long competitionId, long taskId, long version) {
        CompetitionView c = context.lock(competitionId);
        requireEditable(c);
        ContestTask task = task(competitionId, taskId);
        task.requireVersion(version);
        if (c.status() == CompetitionStatus.UPCOMING && tasks.countByCompetitionId(competitionId) <= 1) {
            throw BusinessException.conflict("В опубликованном контесте должно остаться хотя бы одно задание");
        }
        tasks.delete(task);
        tasks.flush();
    }

    @Transactional
    public SubmissionResponse submit(long competitionId, long taskId, SubmissionRequest request) {
        long userId = actor.requireAthleteUserId();
        CompetitionView c = context.lock(competitionId);
        requireContest(c);
        if (c.status() != CompetitionStatus.ONGOING || clock.instant().isBefore(c.startsAt())
                || !clock.instant().isBefore(c.endsAt()) || c.finalizedAt() != null) {
            throw BusinessException.conflict("Приём решений закрыт");
        }
        var participant = context.participant(competitionId, userId);
        task(competitionId, taskId);
        if (submissions.existsByTaskIdAndRegistrationId(taskId, participant.registrationId())) {
            throw BusinessException.conflict("Решение этого задания уже отправлено");
        }
        return SubmissionResponse.from(submissions.saveAndFlush(Submission.create(competitionId, taskId,
                participant.registrationId(), request.answer(), clock.instant())));
    }

    public List<SubmissionResponse> mine(long competitionId) {
        long userId = actor.requireAthleteUserId();
        requireContest(context.get(competitionId));
        var participant = context.participant(competitionId, userId);
        return submissions.findByCompetitionIdAndRegistrationIdOrderByIdAsc(competitionId, participant.registrationId())
                .stream().map(SubmissionResponse::from).toList();
    }

    public PageResponse<SubmissionResponse> submissions(long competitionId, Long taskId, Boolean reviewed, int page, int size) {
        requireOwner(context.get(competitionId));
        if (taskId != null) task(competitionId, taskId);
        return PageResponse.from(submissions.search(competitionId, taskId, reviewed,
                PageRequests.of(page, size, Sort.by("id"))).map(SubmissionResponse::from));
    }

    public SubmissionResponse submission(long competitionId, long submissionId) {
        requireOwner(context.get(competitionId));
        return SubmissionResponse.from(submissionEntity(competitionId, submissionId));
    }

    @Transactional
    public SubmissionResponse review(long competitionId, long submissionId, ReviewRequest request) {
        CompetitionView c = context.lock(competitionId);
        requireOwner(c);
        if (c.finalizedAt() != null || (c.status() != CompetitionStatus.ONGOING && c.status() != CompetitionStatus.COMPLETED)) {
            throw BusinessException.conflict("Оценивание недоступно");
        }
        Submission submission = submissionEntity(competitionId, submissionId);
        ContestTask task = task(competitionId, submission.getTaskId());
        submission.review(request.version(), request.points(), task.getMaxPoints(),
                request.comment(), actor.requireOrganizerId(), clock.instant());
        submissions.flush();
        return SubmissionResponse.from(submission);
    }

    public StandingsResponse standings(long competitionId) {
        CompetitionView c = context.get(competitionId);
        requireContest(c);
        if (c.finalizedAt() == null) throw BusinessException.conflict("Итоги ещё не опубликованы");
        return new StandingsResponse(competitionId, c.finalizedAt(), results.published(competitionId));
    }

    @Transactional
    public StandingsResponse finalizeResults(long competitionId) {
        CompetitionView c = context.lock(competitionId);
        requireOwner(c);
        if (c.finalizedAt() != null) return standings(competitionId);
        if (c.status() != CompetitionStatus.COMPLETED || clock.instant().isBefore(c.endsAt())) {
            throw BusinessException.conflict("Сначала завершите соревнование после окончания времени");
        }
        List<Submission> submitted = submissions.findByCompetitionId(competitionId);
        if (submitted.stream().anyMatch(s -> s.getPoints() == null)) {
            throw BusinessException.conflict("Проверьте все отправленные решения");
        }
        Map<Long, Long> totals = new HashMap<>();
        for (Submission s : submitted) totals.merge(s.getRegistrationId(), s.getPoints().longValue(), Math::addExact);
        var participants = new ArrayList<>(context.participants(competitionId));
        participants.sort(Comparator.<ContestContext.Participant>comparingLong(
                p -> totals.getOrDefault(p.registrationId(), 0L)).reversed().thenComparingLong(ContestContext.Participant::athleteId));
        List<Standing> standings = new ArrayList<>();
        long previous = -1;
        int place = 0;
        for (int i = 0; i < participants.size(); i++) {
            var p = participants.get(i);
            long points = totals.getOrDefault(p.registrationId(), 0L);
            if (points != previous) place = i + 1;
            standings.add(new Standing(p.registrationId(), p.athleteId(), p.fullName(), place, points));
            previous = points;
        }
        results.publish(competitionId, standings);
        context.markFinalized(competitionId, clock.instant());
        return standings(competitionId);
    }

    private ContestTask task(long competitionId, long taskId) {
        return tasks.findById(taskId).filter(t -> t.getCompetitionId() == competitionId)
                .orElseThrow(() -> BusinessException.notFound("Задание не найдено"));
    }

    private Submission submissionEntity(long competitionId, long submissionId) {
        return submissions.findById(submissionId).filter(s -> s.getCompetitionId() == competitionId)
                .orElseThrow(() -> BusinessException.notFound("Решение не найдено"));
    }

    private void requireContest(CompetitionView c) {
        if (!c.enabled()) throw BusinessException.conflict("Соревнование проводится вне платформы");
    }

    private void requireOwner(CompetitionView c) {
        long owner = actor.requireOrganizerId();
        requireContest(c);
        if (c.ownerId() != owner) throw BusinessException.forbidden("Контест принадлежит другому организатору");
    }

    private void requireEditable(CompetitionView c) {
        requireOwner(c);
        if (c.status() != CompetitionStatus.DRAFT
                && (c.status() != CompetitionStatus.UPCOMING || !clock.instant().isBefore(c.startsAt()))) {
            throw BusinessException.conflict("Задания можно менять только до начала");
        }
    }

    private void requireParticipantAccess(CompetitionView c) {
        requireContest(c);
        var principal = actor.getPrincipal();
        if (principal.getRole() == UserRole.ORGANIZER) {
            requireOwner(c);
            return;
        }
        if (c.status() != CompetitionStatus.ONGOING && c.status() != CompetitionStatus.COMPLETED
                || clock.instant().isBefore(c.startsAt())) {
            throw BusinessException.conflict("Задания недоступны до старта или после отмены");
        }
        context.participant(c.id(), actor.requireAthleteUserId());
    }
}

