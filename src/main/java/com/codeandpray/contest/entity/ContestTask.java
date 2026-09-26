package com.codeandpray.contest.entity;

import com.codeandpray.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contest_tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContestTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @Column(name = "competition_id", nullable = false, updatable = false)
    private long competitionId;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, length = 20000)
    private String statement;
    @Column(name = "max_points", nullable = false)
    private int maxPoints;
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public static ContestTask create(long competitionId, String title, String statement, int maxPoints, int sortOrder) {
        ContestTask task = new ContestTask();
        task.competitionId = competitionId;
        task.change(title, statement, maxPoints, sortOrder);
        return task;
    }

    public void change(String title, String statement, int maxPoints, int sortOrder) {
        if (title == null || title.isBlank() || title.strip().length() > 200
                || statement == null || statement.isBlank() || statement.strip().length() > 20000
                || maxPoints < 1 || maxPoints > 1000000 || sortOrder < 0) {
            throw BusinessException.badRequest("Некорректное задание");
        }
        this.title = title.strip();
        this.statement = statement.strip();
        this.maxPoints = maxPoints;
        this.sortOrder = sortOrder;
    }

    public void requireVersion(long expected) {
        if (version == null || version != expected) throw BusinessException.conflict("Задание изменилось");
    }
}

