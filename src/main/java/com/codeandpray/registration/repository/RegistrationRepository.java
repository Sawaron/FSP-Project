package com.codeandpray.registration.repository;

import com.codeandpray.registration.entity.Registration;
import com.codeandpray.registration.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByCompetitionIdAndAthleteId(Long competitionId, Long athleteId);
    List<Registration> findAllByAthleteIdOrderByRegisteredAtDesc(Long athleteId);
    List<Registration> findAllByCompetitionIdAndStatusOrderByRegisteredAtAsc(Long competitionId, RegistrationStatus status);
    boolean existsByCompetitionId(Long competitionId);
}