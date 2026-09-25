package com.codeandpray.result.entity;

import com.codeandpray.result.enums.ResultStatus;
import com.codeandpray.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "results",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_results_registration",
                columnNames = "registration_id"
        )
)
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "registration_id", nullable = false, updatable = false)
    private long registrationId;

    @Column(name = "place")
    private Integer place;

    @Column(name = "performance_value", precision = 19, scale = 4)
    private BigDecimal performanceValue;

    @Column(name = "performance_unit", length = 30)
    private String performanceUnit;

    @Column(name = "rating_points")
    private Long ratingPoints;

    @Column(name = "formula_version")
    private Integer formulaVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultStatus status;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "entered_by_user_id", nullable = false, updatable = false)
    private long enteredByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Result createDraft(
            long registrationId,
            long organizerId,
            Integer place,
            BigDecimal performanceValue,
            String performanceUnit,
            Instant now
    ) {
        if (registrationId <= 0) {
            throw BusinessException.badRequest("Некорректный идентификатор заявки");
        }
        if (organizerId <= 0) {
            throw BusinessException.badRequest("Некорректный идентификатор организатора");
        }

        requireTime(now);

        Result result = new Result();
        result.registrationId = registrationId;
        result.enteredByUserId = organizerId;
        result.status = ResultStatus.DRAFT;
        result.createdAt = now;
        result.updatedAt = now;
        result.applyPerformance(place, performanceValue, performanceUnit);

        return result;
    }

    public void updateDraft(
            Integer place,
            BigDecimal performanceValue,
            String performanceUnit,
            Instant now
    ) {
        if (status != ResultStatus.DRAFT) {
            throw BusinessException.conflict(
                    "Опубликованный результат нельзя редактировать как черновик"
            );
        }

        requireTime(now);
        applyPerformance(place, performanceValue, performanceUnit);
        updatedAt = now;
    }

    public boolean publish(
            long calculatedRatingPoints,
            int formulaVersion,
            Instant now
    ) {
        if (status == ResultStatus.PUBLISHED) {
            return false;
        }

        requireTime(now);
        requireRating(calculatedRatingPoints, formulaVersion);

        ratingPoints = calculatedRatingPoints;
        this.formulaVersion = formulaVersion;
        status = ResultStatus.PUBLISHED;
        publishedAt = now;
        updatedAt = now;

        return true;
    }

    public void correctPublished(
            Integer place,
            BigDecimal performanceValue,
            String performanceUnit,
            long calculatedRatingPoints,
            int formulaVersion,
            Instant now
    ) {
        if (status != ResultStatus.PUBLISHED) {
            throw BusinessException.conflict("Результат ещё не опубликован");
        }

        requireTime(now);
        requireRating(calculatedRatingPoints, formulaVersion);
        applyPerformance(place, performanceValue, performanceUnit);

        ratingPoints = calculatedRatingPoints;
        this.formulaVersion = formulaVersion;
        updatedAt = now;
    }

    public void requireDraft() {
        if (status != ResultStatus.DRAFT) {
            throw BusinessException.conflict("Операция разрешена только для черновика");
        }
    }

    public void requireVersion(long expectedVersion) {
        if (version == null || version.longValue() != expectedVersion) {
            throw BusinessException.conflict("Результат изменился: обновите страницу");
        }
    }

    private void applyPerformance(
            Integer place,
            BigDecimal performanceValue,
            String performanceUnit
    ) {
        if (place != null && place <= 0) {
            throw BusinessException.badRequest("Место должно быть положительным");
        }

        String normalizedUnit = performanceUnit == null
                ? null
                : performanceUnit.strip();

        if (normalizedUnit != null && normalizedUnit.isEmpty()) {
            normalizedUnit = null;
        }

        if ((performanceValue == null) != (normalizedUnit == null)) {
            throw BusinessException.badRequest(
                    "Показатель результата и его единица должны быть указаны вместе"
            );
        }

        if (place == null && performanceValue == null) {
            throw BusinessException.badRequest(
                    "Укажите место или числовой показатель результата"
            );
        }

        if (normalizedUnit != null && normalizedUnit.length() > 30) {
            throw BusinessException.badRequest(
                    "Единица измерения не должна превышать 30 символов"
            );
        }

        if (performanceValue != null) {
            BigDecimal normalizedValue = performanceValue.stripTrailingZeros();
            int integerDigits = normalizedValue.precision()
                    - normalizedValue.scale();

            if (normalizedValue.scale() > 4 || integerDigits > 15) {
                throw BusinessException.badRequest(
                        "Показатель допускает до 15 цифр до запятой и до 4 после"
                );
            }
        }

        this.place = place;
        this.performanceValue = performanceValue;
        this.performanceUnit = normalizedUnit;
    }

    private static void requireRating(long points, int formulaVersion) {
        if (points < 0) {
            throw BusinessException.badRequest("Баллы не могут быть отрицательными");
        }
        if (formulaVersion <= 0) {
            throw BusinessException.badRequest("Версия формулы должна быть положительной");
        }
    }

    private static void requireTime(Instant now) {
        if (now == null) {
            throw BusinessException.badRequest("Не указано время операции");
        }
    }

}