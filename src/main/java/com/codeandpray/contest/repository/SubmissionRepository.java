package com.codeandpray.contest.repository;

import com.codeandpray.contest.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByTaskIdAndRegistrationId(long taskId, long registrationId);
    List<Submission> findByCompetitionId(long competitionId);
    List<Submission> findByCompetitionIdAndRegistrationIdOrderByIdAsc(long competitionId, long registrationId);

    @Query("select s from Submission s where s.competitionId = :competitionId "
            + "and (:taskId is null or s.taskId = :taskId) "
            + "and (:reviewed is null or (:reviewed = true and s.points is not null) "
            + "or (:reviewed = false and s.points is null))")
    Page<Submission> search(@Param("competitionId") long competitionId, @Param("taskId") Long taskId,
                            @Param("reviewed") Boolean reviewed, Pageable pageable);
}

