package com.codeandpray.competition.repository;

import com.codeandpray.competition.entity.Competition;
import com.codeandpray.competition.enums.CompetitionStatus;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    @Query("select c from Competition c where c.status <> com.codeandpray.competition.enums.CompetitionStatus.DRAFT and (:status is null or c.status = :status) and (:disciplineId is null or c.disciplineId = :disciplineId)")
    Page<Competition> search(@Param("status") CompetitionStatus status, @Param("disciplineId") Long disciplineId, Pageable page);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Competition c where c.id = :id")
    Optional<Competition> findForUpdate(@Param("id") long id);

    Page<Competition> findByCreatedByUserId(long userId, Pageable page);
}
