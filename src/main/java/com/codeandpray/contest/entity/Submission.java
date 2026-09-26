package com.codeandpray.contest.entity;

import com.codeandpray.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(name = "contest_submissions", uniqueConstraints =
        @UniqueConstraint(name = "uk_submission_task_registration", columnNames = {"task_id", "registration_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @Column(name = "competition_id", nullable = false, updatable = false)
    private long competitionId;
    @Column(name = "task_id", nullable = false, updatable = false)
    private long taskId;
    @Column(name = "registration_id", nullable = false, updatable = false)
    private long registrationId;
    @Column(nullable = false, length = 50000, updatable = false)
    private String answer;
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;
    private Integer points;
    @Column(name = "review_comment", length = 5000)
    private String reviewComment;
    @Column(name = "reviewed_by")
    private Long reviewedBy;
    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    public static Submission create(long competitionId, long taskId, long registrationId, String answer, Instant now) {
        if (answer == null || answer.isBlank() || answer.strip().length() > 50000) {
            throw BusinessException.badRequest("Укажите решение до 50000 символов");
        }
        Submission s = new Submission();
        s.competitionId = competitionId;
        s.taskId = taskId;
        s.registrationId = registrationId;
        s.answer = answer.strip();
        s.submittedAt = now;
        return s;
    }

    public void review(long expectedVersion, int points, int maximum, String comment, long reviewer, Instant now) {
        if (version == null || version != expectedVersion) throw BusinessException.conflict("Решение уже изменилось");
        if (points < 0 || points > maximum || (comment != null && comment.length() > 5000)) {
            throw BusinessException.badRequest("Оценка должна быть от 0 до максимума задания");
        }
        this.points = points;
        this.reviewComment = comment;
        this.reviewedBy = reviewer;
        this.reviewedAt = now;
    }
}

