package com.codeandpray.rating.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "rating_snapshots")
public class RatingSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "athlete_id", nullable = false, updatable = false)
    private long athleteId;

    @Column(name = "result_points", nullable = false, updatable = false)
    private long resultPoints;

    @Column(name = "qualification_points", nullable = false, updatable = false)
    private long qualificationPoints;

    @Column(name = "total_points", nullable = false, updatable = false)
    private long totalPoints;

    @Column(name = "calculated_at", nullable = false, updatable = false)
    private Instant calculatedAt;

    @Column(name = "formula_version", nullable = false, updatable = false)
    private int formulaVersion;

    protected RatingSnapshot() {
    }

    public static RatingSnapshot create(long athleteId, long resultPoints, long qualificationPoints,
                                        Instant calculatedAt, int formulaVersion) {
        if (athleteId <= 0 || resultPoints < 0 || qualificationPoints < 0 || calculatedAt == null || formulaVersion <= 0) {
            throw new IllegalArgumentException("Некорректные данные снимка рейтинга");
        }
        RatingSnapshot s = new RatingSnapshot();
        s.athleteId = athleteId;
        s.resultPoints = resultPoints;
        s.qualificationPoints = qualificationPoints;
        s.totalPoints = Math.addExact(resultPoints, qualificationPoints);
        s.calculatedAt = calculatedAt;
        s.formulaVersion = formulaVersion;
        return s;
    }

    public Long getId() {
        return id;
    }

    public long getAthleteId() {
        return athleteId;
    }

    public long getResultPoints() {
        return resultPoints;
    }

    public long getQualificationPoints() {
        return qualificationPoints;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public int getFormulaVersion() {
        return formulaVersion;
    }
}
