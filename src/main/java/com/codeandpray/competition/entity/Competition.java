package com.codeandpray.competition.entity;

import com.codeandpray.common.exception.BusinessException;
import com.codeandpray.competition.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "competitions")
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CompetitionLevel level;

    @Column(name = "discipline_id", nullable = false)
    private long disciplineId;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompetitionFormat format;

    @Column(length = 500)
    private String venue;

    @Column(nullable = false, length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompetitionStatus status;

    @Column(name = "registration_opens_at", nullable = false)
    private Instant registrationOpensAt;

    @Column(name = "registration_closes_at", nullable = false)
    private Instant registrationClosesAt;

    @Column(name = "created_by_user_id", nullable = false, updatable = false)
    private long createdByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Competition create(CompetitionDetails details, long organizerId, Instant now) {
        if (organizerId <= 0) throw BusinessException.badRequest("Некорректный организатор");
        if (!details.startsAt().isAfter(now) || !details.registrationClosesAt().isAfter(now)) {
            throw BusinessException.badRequest("Новое соревнование и закрытие регистрации должны быть в будущем");
        }
        Competition competition = new Competition();
        competition.createdByUserId = organizerId;
        competition.status = CompetitionStatus.UPCOMING;
        competition.apply(details);
        competition.createdAt = now;
        competition.updatedAt = now;
        return competition;
    }

    public void requireOwner(long userId) {
        if (createdByUserId != userId) throw BusinessException.forbidden("Управлять можно только своим соревнованием");
    }

    public void requireVersion(long expectedVersion) {
        if (version == null || version != expectedVersion) {
            throw BusinessException.conflict("Соревнование изменилось: обновите карточку");
        }
    }

    public void updateDetails(CompetitionDetails details, boolean hasRegistrations, Instant now) {
        if (status != CompetitionStatus.UPCOMING || !now.isBefore(startsAt)) {
            throw BusinessException.conflict("Редактирование доступно до начала предстоящего соревнования");
        }
        if (!details.startsAt().isAfter(now)) throw BusinessException.badRequest("Начало должно быть в будущем");
        if (hasRegistrations && (disciplineId != details.disciplineId() || level != details.level())) {
            throw BusinessException.conflict("После появления заявок нельзя менять дисциплину и уровень");
        }
        apply(details);
        updatedAt = now;
    }

    public void changeStatus(CompetitionStatus target, Instant now) {
        if (target == null) throw BusinessException.badRequest("Укажите статус");
        if (target == status) return;
        boolean allowed = switch (status) {
            case UPCOMING -> target == CompetitionStatus.CANCELLED
                    || (target == CompetitionStatus.ONGOING && !now.isBefore(startsAt));
            case ONGOING -> target == CompetitionStatus.CANCELLED
                    || (target == CompetitionStatus.COMPLETED && !now.isBefore(endsAt));
            case COMPLETED, CANCELLED -> false;
        };
        if (!allowed)
            throw BusinessException.conflict("Недопустимый переход статуса или ещё не наступило нужное время");
        status = target;
        updatedAt = now;
    }

    private void apply(CompetitionDetails d) {
        title = d.title();
        level = d.level();
        disciplineId = d.disciplineId();
        startsAt = d.startsAt();
        endsAt = d.endsAt();
        format = d.format();
        venue = d.venue();
        description = d.description();
        registrationOpensAt = d.registrationOpensAt();
        registrationClosesAt = d.registrationClosesAt();
    }

    public boolean isRegistrationOpen(Instant now) {
        return status == CompetitionStatus.UPCOMING && !now.isBefore(registrationOpensAt)
                && now.isBefore(registrationClosesAt);
    }

}