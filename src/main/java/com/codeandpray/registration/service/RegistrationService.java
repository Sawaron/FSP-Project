package com.codeandpray.registration.service;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.security.CurrentActor;
import com.codeandpray.common.web.*;
import com.codeandpray.registration.dto.RegistrationResponse;
import com.codeandpray.registration.dto.AthleteSummaryResponse;
import com.codeandpray.registration.entity.Registration;
import com.codeandpray.registration.enums.RegistrationStatus;
import com.codeandpray.registration.port.RegistrationContext;
import com.codeandpray.registration.port.RegistrationAthleteDirectory;
import com.codeandpray.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistrationService {
    private final RegistrationRepository repository;
    private final CurrentActor actor;
    private final RegistrationContext context;
    private final RegistrationAthleteDirectory athletes;
    private final Clock clock;

    @Transactional
    public RegistrationResponse submitRegistration(Long competitionId) {
        positive(competitionId);
        long athleteId = context.athleteIdForUser(actor.requireAthleteUserId());
        var competition = context.lockCompetition(competitionId);
        context.lockAthlete(athleteId);
        if (!competition.registrationOpen(clock.instant())) {
            throw BusinessException.conflict("Регистрация на соревнование закрыта");
        }
        var existing = repository.findByCompetitionIdAndAthleteId(competitionId, athleteId);
        Registration registration;
        if (existing.isPresent()) {
            registration = existing.get();
            if (registration.getStatus() == RegistrationStatus.REGISTERED) {
                throw BusinessException.conflict("Вы уже зарегистрированы");
            }
            registration.restore();
        } else {
            registration = Registration.create(competitionId, athleteId, clock.instant());
        }
        return toDto(repository.saveAndFlush(registration), summary(athleteId));
    }

    @Transactional
    public RegistrationResponse cancelRegistration(Long id) {
        positive(id);
        long athleteId = context.athleteIdForUser(actor.requireAthleteUserId());
        long competitionId = repository.findCompetitionId(id)
                .orElseThrow(() -> BusinessException.notFound("Заявка не найдена"));
        var competition = context.lockCompetition(competitionId);
        context.lockAthlete(athleteId);
        Registration registration = repository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Заявка не найдена"));
        if (registration.getAthleteId() != athleteId) {
            throw BusinessException.forbidden("Нельзя отменить чужую заявку");
        }
        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            return toDto(registration, summary(athleteId));
        }
        if (!competition.registrationOpen(clock.instant())) {
            throw BusinessException.conflict("Срок отмены заявки истёк");
        }
        registration.cancel();
        repository.flush();
        return toDto(registration, summary(athleteId));
    }

    public PageResponse<RegistrationResponse> getMyRegistrations(int page, int size) {
        long athleteId = context.athleteIdForUser(actor.requireAthleteUserId());
        return toPage(repository.findByAthleteId(athleteId,
                PageRequests.of(page, size, Sort.by("registeredAt").descending()
                        .and(Sort.by("id").descending()))));
    }

    public PageResponse<RegistrationResponse> getCompetitionParticipants(
            Long competitionId, int page, int size) {
        positive(competitionId);
        long organizerId = actor.requireOrganizerId();
        var competition = context.getCompetition(competitionId);
        if (competition.organizerId() != organizerId) {
            throw BusinessException.forbidden("Недоступны участники чужого соревнования");
        }
        return toPage(repository.findByCompetitionIdAndStatus(
                        competitionId, RegistrationStatus.REGISTERED,
                        PageRequests.of(page, size, Sort.by("registeredAt").and(Sort.by("id")))));
    }

    private PageResponse<RegistrationResponse> toPage(Page<Registration> page) {
        Set<Long> athleteIds = page.getContent().stream()
                .map(Registration::getAthleteId)
                .collect(Collectors.toSet());
        Map<Long, RegistrationAthleteDirectory.AthleteSummary> summaries = athletes.findAll(athleteIds);
        return PageResponse.from(page.map(registration ->
                toDto(registration, requireSummary(registration.getAthleteId(), summaries))));
    }

    private RegistrationResponse toDto(
            Registration registration,
            RegistrationAthleteDirectory.AthleteSummary athlete
    ) {
        return new RegistrationResponse(registration.getId(), registration.getCompetitionId(),
                registration.getAthleteId(), new AthleteSummaryResponse(
                athlete.id(), athlete.fullName(), athlete.city(),
                athlete.organization(), athlete.qualification()),
                registration.getStatus(), registration.getRegisteredAt());
    }

    private RegistrationAthleteDirectory.AthleteSummary summary(long athleteId) {
        return requireSummary(athleteId, athletes.findAll(Set.of(athleteId)));
    }

    private RegistrationAthleteDirectory.AthleteSummary requireSummary(
            long athleteId,
            Map<Long, RegistrationAthleteDirectory.AthleteSummary> summaries
    ) {
        RegistrationAthleteDirectory.AthleteSummary summary = summaries.get(athleteId);
        if (summary == null) {
            throw BusinessException.notFound("Профиль спортсмена не найден");
        }
        return summary;
    }

    private static void positive(Long id) {
        if (id == null || id <= 0) {
            throw BusinessException.badRequest("Идентификатор должен быть положительным");
        }
    }
}
