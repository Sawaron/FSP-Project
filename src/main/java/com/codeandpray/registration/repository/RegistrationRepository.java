package com.codeandpray.registration.repository;

import com.codeandpray.registration.entity.Registration;
import com.codeandpray.registration.enums.RegistrationStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByCompetitionIdAndAthleteId(Long competitionId, Long athleteId);
    Page<Registration> findByAthleteId(Long athleteId, Pageable pageable);
    Page<Registration> findByCompetitionIdAndStatus(
            Long competitionId, RegistrationStatus status, Pageable pageable);
    boolean existsByCompetitionId(Long competitionId);

    @Query("select r.competitionId from Registration r where r.id = :id")
    Optional<Long> findCompetitionId(@Param("id") long id);
}