package com.codeandpray.result.entity;

import com.codeandpray.result.enums.ResultStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

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

    protected Result() {
    }

    public static Result createDraft(
            long registrationId,
            long organizerId,
            Integer place,
            BigDecimal performanceValue,
            String performanceUnit,
            Instant now
    ) {
        if (registrationId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор заявки");
        }
        if (organizerId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор организатора");
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
            throw new IllegalStateException(
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
            throw new IllegalStateException("Результат ещё не опубликован");
        }

        requireTime(now);
        requireRating(calculatedRatingPoints, formulaVersion);
        applyPerformance(place, performanceValue, performanceUnit);

        ratingPoints = calculatedRatingPoints;
        this.formulaVersion = formulaVersion;
        updatedAt = now;
    }

    private void applyPerformance(
            Integer place,
            BigDecimal performanceValue,
            String performanceUnit
    ) {
        if (place != null && place <= 0) {
            throw new IllegalArgumentException("Место должно быть положительным");
        }

        String normalizedUnit = performanceUnit == null
                ? null
                : performanceUnit.strip();

        if (normalizedUnit != null && normalizedUnit.isEmpty()) {
            normalizedUnit = null;
        }

        if ((performanceValue == null) != (normalizedUnit == null)) {
            throw new IllegalArgumentException(
                    "Показатель результата и его единица должны быть указаны вместе"
            );
        }

        if (place == null && performanceValue == null) {
            throw new IllegalArgumentException(
                    "Укажите место или числовой показатель результата"
            );
        }

        if (normalizedUnit != null && normalizedUnit.length() > 30) {
            throw new IllegalArgumentException(
                    "Единица измерения не должна превышать 30 символов"
            );
        }

        if (performanceValue != null) {
            BigDecimal normalizedValue = performanceValue.stripTrailingZeros();
            int integerDigits = normalizedValue.precision()
                    - normalizedValue.scale();

            if (normalizedValue.scale() > 4 || integerDigits > 15) {
                throw new IllegalArgumentException(
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
            throw new IllegalArgumentException("Баллы не могут быть отрицательными");
        }
        if (formulaVersion <= 0) {
            throw new IllegalArgumentException("Версия формулы должна быть положительной");
        }
    }

    private static void requireTime(Instant now) {
        if (now == null) {
            throw new IllegalArgumentException("Не указано время операции");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public long getRegistrationId() {
        return registrationId;
    }

    public Integer getPlace() {
        return place;
    }

    public BigDecimal getPerformanceValue() {
        return performanceValue;
    }

    public String getPerformanceUnit() {
        return performanceUnit;
    }

    public Long getRatingPoints() {
        return ratingPoints;
    }

    public Integer getFormulaVersion() {
        return formulaVersion;
    }

    public ResultStatus getStatus() {
        return status;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public long getEnteredByUserId() {
        return enteredByUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}