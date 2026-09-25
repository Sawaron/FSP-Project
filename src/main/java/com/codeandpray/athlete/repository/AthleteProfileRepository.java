package com.codeandpray.athlete.repository;

import com.codeandpray.athlete.entity.AthleteProfile;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AthleteProfileRepository extends JpaRepository<AthleteProfile, Long> {
    Optional<AthleteProfile> findByUserId(long userId);

    boolean existsByUserId(long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select profile from AthleteProfile profile where profile.userId = :userId")
    Optional<AthleteProfile> lockByUserId(@Param("userId") long userId);
}
