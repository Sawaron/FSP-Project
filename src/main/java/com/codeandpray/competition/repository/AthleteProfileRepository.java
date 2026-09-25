package com.codeandpray.competition.repository;


import com.codeandpray.athlete.AthleteProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AthleteProfileRepository extends JpaRepository<AthleteProfile, Long> {

    Optional<AthleteProfile> findByUserId(Long userId);

    // можно добавлять произвольные методы по имени — Spring сам сгенерирует запрос
    List<AthleteProfile> findByCity(String city);
}