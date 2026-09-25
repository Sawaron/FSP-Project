package com.codeandpray.registration.service;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.common.security.CurrentActor;
import com.codeandpray.registration.dto.RegistrationResponse;
import com.codeandpray.registration.entity.Registration;
import com.codeandpray.registration.enums.RegistrationStatus;
import com.codeandpray.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final CurrentActor actor;
    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    // Спортсмен: подать заявку или восстановить отменённую
    @Transactional
    public RegistrationResponse submitRegistration(Long competitionId) {
        Long userId = actor.requireAthleteUserId();
        Long athleteProfileId = getAthleteProfileId(userId);
        Instant now = clock.instant();

        checkRegistrationWindow(competitionId, now);

        return registrationRepository.findByCompetitionIdAndAthleteId(competitionId, athleteProfileId)
                .map(existing -> {
                    if (existing.getStatus() == RegistrationStatus.REGISTERED) {
                        throw BusinessException.conflict("Вы уже зарегистрированы на данное соревнование");
                    }
                    // По ТЗ: повторная подача восстанавливает старую запись
                    existing.setStatus(RegistrationStatus.REGISTERED);
                    existing.setRegisteredAt(now);
                    return toDto(registrationRepository.save(existing));
                })
                .orElseGet(() -> {
                    Registration reg = Registration.builder()
                            .competitionId(competitionId)
                            .athleteId(athleteProfileId)
                            .status(RegistrationStatus.REGISTERED)
                            .registeredAt(now)
                            .build();
                    return toDto(registrationRepository.save(reg));
                });
    }

    // Спортсмен: отменить свою заявку
    @Transactional
    public RegistrationResponse cancelRegistration(Long registrationId) {
        Long userId = actor.requireAthleteUserId();
        Long athleteProfileId = getAthleteProfileId(userId);

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> BusinessException.notFound("Заявка не найдена"));

        if (!registration.getAthleteId().equals(athleteProfileId)) {
            throw BusinessException.forbidden("Нельзя отменить чужую заявку");
        }

        checkRegistrationWindow(registration.getCompetitionId(), clock.instant());

        registration.setStatus(RegistrationStatus.CANCELLED);
        return toDto(registrationRepository.save(registration));
    }

    // Спортсмен: список моих соревнований и заявок
    public List<RegistrationResponse> getMyRegistrations() {
        Long userId = actor.requireAthleteUserId();
        Long athleteProfileId = getAthleteProfileId(userId);
        return registrationRepository.findAllByAthleteIdOrderByRegisteredAtDesc(athleteProfileId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Организатор: список зарегистрированных участников
    public List<RegistrationResponse> getCompetitionParticipants(Long competitionId) {
        actor.requireOrganizerId();
        return registrationRepository.findAllByCompetitionIdAndStatusOrderByRegisteredAtAsc(competitionId, RegistrationStatus.REGISTERED)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Метод для модуля соревнований (проверка перед удалением/изменением)
    public boolean hasAnyRegistration(long competitionId) {
        return registrationRepository.existsByCompetitionId(competitionId);
    }

    // Метод для модуля результатов (привязать результат к заявке)
    public Optional<Registration> getRegistrationById(long registrationId) {
        return registrationRepository.findById(registrationId);
    }

    private void checkRegistrationWindow(Long competitionId, Instant now) {
        List<Timestamp> closesAtList = jdbcTemplate.query(
                "SELECT registration_opens_at, registration_closes_at FROM competitions WHERE id = ?",
                (rs, rowNum) -> rs.getTimestamp("registration_closes_at"),
                competitionId
        );

        if (closesAtList.isEmpty()) {
            throw BusinessException.notFound("Соревнование не найдено");
        }

        Timestamp opensAt = jdbcTemplate.queryForObject(
                "SELECT registration_opens_at FROM competitions WHERE id = ?",
                Timestamp.class,
                competitionId
        );
        Timestamp closesAt = closesAtList.get(0);

        if (now.isBefore(opensAt.toInstant()) || now.isAfter(closesAt.toInstant())) {
            throw BusinessException.badRequest("Окно регистрации закрыто");
        }
    }

    private Long getAthleteProfileId(Long userId) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT id FROM athlete_profiles WHERE user_id = ?",
                (rs, rowNum) -> rs.getLong("id"),
                userId
        );
        if (ids.isEmpty()) {
            throw BusinessException.badRequest("Профиль спортсмена ещё не заполнен");
        }
        return ids.get(0);
    }

    private RegistrationResponse toDto(Registration r) {
        return new RegistrationResponse(
                r.getId(),
                r.getCompetitionId(),
                r.getAthleteId(),
                r.getStatus(),
                r.getRegisteredAt()
        );
    }
}